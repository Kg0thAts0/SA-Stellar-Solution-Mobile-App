using System;
using System.Data;
using System.Linq;
using System.Security.Claims;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Production;
using QAClothingFactory.API.Models;

namespace QAClothingFactory.API.Controllers
{
    // Separate controller intentionally preserves all existing production endpoints.
    [ApiController]
    [Route("api/production/quotations")]
    [Authorize(Roles = "ProductionManager,Admin")]
    public sealed class QuotationProductionController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public QuotationProductionController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet("{quotationId:int}/batches")]
        public async Task<IActionResult> GetQuotationBatches(int quotationId, CancellationToken ct)
        {
            var quotation = await _context.CustomerQuotations.AsNoTracking()
                .Where(q => q.QuotationID == quotationId)
                .Select(q => new { q.QuotationID, q.ReferenceNumber, q.Quantity, q.Status, q.CustomerDecision })
                .FirstOrDefaultAsync(ct);

            if (quotation == null)
                return NotFound(new { success = false, message = "Quotation not found." });

            var batches = await _context.ProductionBatches.AsNoTracking()
                .Where(b => b.QuotationID == quotationId)
                .OrderBy(b => b.BatchID)
                .Select(b => new
                {
                    b.BatchID,
                    b.BatchNumber,
                    b.ProductID,
                    b.TargetQuantity,
                    b.QuantityProduced,
                    b.ProductionStatus,
                    b.ProductionDate,
                    b.Shift,
                    b.LineNumber,
                    b.SupervisorID
                })
                .ToListAsync(ct);

            var allocated = batches.Where(b => b.ProductionStatus != "Cancelled")
                .Sum(b => b.TargetQuantity);

            return Ok(new
            {
                success = true,
                quotation,
                batches,
                allocatedQuantity = allocated,
                remainingQuantity = Math.Max(0m, quotation.Quantity - allocated)
            });
        }

        [HttpPost("{quotationId:int}/create-batch")]
        public async Task<IActionResult> CreateBatchFromQuotation(
            int quotationId,
            [FromBody] CreateBatchFromQuotationRequest request,
            CancellationToken ct)
        {
            if (!ModelState.IsValid)
                return ValidationProblem(ModelState);

            if (quotationId <= 0)
                return BadRequest(new { success = false, message = "Invalid quotation ID." });

            var batchNumber = request.BatchNumber?.Trim() ?? "";
            var productCode = request.ProductCode?.Trim() ?? "";
            var productName = request.ProductName?.Trim() ?? "";
            var unit = request.Unit?.Trim() ?? "";
            var shift = request.Shift?.Trim();
            var lineNumber = request.LineNumber?.Trim();

            if (string.IsNullOrWhiteSpace(batchNumber))
                return BadRequest(new { success = false, message = "Batch number is required." });

            if (request.TargetQuantity <= 0 || decimal.Round(request.TargetQuantity, 2) != request.TargetQuantity)
                return BadRequest(new { success = false, message = "Target quantity must be positive and have at most two decimal places." });

            if (request.ProductionDate == default)
                return BadRequest(new { success = false, message = "Production date is required." });

            if (request.StartTime.HasValue && request.EndTime.HasValue && request.EndTime < request.StartTime)
                return BadRequest(new { success = false, message = "End time cannot be earlier than start time." });

            if (!string.IsNullOrWhiteSpace(shift))
            {
                var allowed = new[] { "Morning", "Afternoon", "Night" };
                shift = allowed.FirstOrDefault(s => s.Equals(shift, StringComparison.OrdinalIgnoreCase));
                if (shift == null)
                    return BadRequest(new { success = false, message = "Shift must be Morning, Afternoon or Night." });
            }

            if (request.ProductID.HasValue && request.ProductID.Value <= 0)
                return BadRequest(new { success = false, message = "ProductID must be positive." });

            if (!request.ProductID.HasValue &&
                (string.IsNullOrWhiteSpace(productCode) || string.IsNullOrWhiteSpace(productName) || string.IsNullOrWhiteSpace(unit)))
                return BadRequest(new { success = false, message = "For a new product, ProductCode, ProductName and Unit are required." });

            if (request.UnitPrice.HasValue &&
                decimal.Round(request.UnitPrice.Value, 2) != request.UnitPrice.Value)
                return BadRequest(new { success = false, message = "UnitPrice may have at most two decimal places." });

            var claim = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (!int.TryParse(claim, out var employeeId))
                return Unauthorized(new { success = false, message = "Unable to identify the authenticated employee." });

            var activeManager = await _context.Employees.AsNoTracking().AnyAsync(e =>
                e.EmployeeID == employeeId && e.EmployeeStatus == "Active" &&
                (e.Role == "ProductionManager" || e.Role == "Admin"), ct);
            if (!activeManager)
                return StatusCode(403, new { success = false, message = "An active Production Manager or Admin is required." });

            if (request.SupervisorID.HasValue)
            {
                var activeSupervisor = await _context.Employees.AsNoTracking().AnyAsync(e =>
                    e.EmployeeID == request.SupervisorID.Value &&
                    e.EmployeeStatus == "Active" && e.Role == "Supervisor", ct);
                if (!activeSupervisor)
                    return BadRequest(new { success = false, message = "The supervisor must be active and have the Supervisor role." });
            }

            // SERIALIZABLE transactions protect the allocation check from two
            // concurrent calls to this endpoint scheduling the same remaining quantity.
            await using var transaction = await _context.Database.BeginTransactionAsync(
                IsolationLevel.Serializable, ct);

            try
            {
                var quotation = await _context.CustomerQuotations
                    .FirstOrDefaultAsync(q => q.QuotationID == quotationId, ct);

                if (quotation == null)
                    return NotFound(new { success = false, message = "Quotation not found." });

                if (quotation.Status != "Approved" || quotation.CustomerDecision != "Accepted")
                    return Conflict(new { success = false, message = "The quotation must be approved by the manager and accepted by the customer before production can be scheduled." });

                var allocated = await _context.ProductionBatches.AsNoTracking()
                    .Where(b => b.QuotationID == quotationId && b.ProductionStatus != "Cancelled")
                    .SumAsync(b => (decimal?)b.TargetQuantity, ct) ?? 0m;

                var remaining = (decimal)quotation.Quantity - allocated;
                if (request.TargetQuantity > remaining)
                    return Conflict(new
                    {
                        success = false,
                        message = "Requested batch quantity exceeds the remaining quantity on this quotation.",
                        orderedQuantity = quotation.Quantity,
                        allocatedQuantity = allocated,
                        remainingQuantity = Math.Max(0m, remaining)
                    });

                if (await _context.ProductionBatches.AnyAsync(
                    b => b.BatchNumber.ToLower() == batchNumber.ToLower(), ct))
                    return Conflict(new { success = false, message = "This batch number already exists." });

                FinishedGood product;
                bool productCreated = false;

                if (request.ProductID.HasValue)
                {
                    var existing = await _context.FinishedGoods
                        .FirstOrDefaultAsync(p => p.ProductID == request.ProductID.Value, ct);
                    if (existing == null)
                        return BadRequest(new { success = false, message = "The selected product does not exist." });
                    product = existing;
                }
                else
                {
                    var existing = await _context.FinishedGoods
                        .FirstOrDefaultAsync(p => p.ProductCode.ToLower() == productCode.ToLower(), ct);
                    if (existing != null)
                    {
                        // Do not silently overwrite a registered product's identity or price.
                        if (!existing.ProductName.Equals(productName, StringComparison.OrdinalIgnoreCase) ||
                            !existing.Unit.Equals(unit, StringComparison.OrdinalIgnoreCase))
                            return Conflict(new { success = false, message = "Product code already exists with different product details. Select that product by ProductID or use another code." });
                        product = existing;
                    }
                    else
                    {
                        product = new FinishedGood
                        {
                            ProductCode = productCode,
                            ProductName = productName,
                            Category = quotation.Category.Length <= 50 ? quotation.Category : quotation.Category[..50],
                            Unit = unit,
                            CurrentStock = 0m,
                            UnitPrice = request.UnitPrice ?? 0m,
                            BatchID = null,
                            ProductionBatchID = null,
                            QualityStatus = "Pending",
                            RecordedBy = employeeId,
                            RecordedAt = DateTime.SpecifyKind(DateTime.UtcNow, DateTimeKind.Unspecified)
                        };
                        _context.FinishedGoods.Add(product);
                        await _context.SaveChangesAsync(ct);
                        productCreated = true;
                    }
                }

                // Most existing production timestamps are PostgreSQL timestamp WITHOUT time zone.
                var productionDate = DateTime.SpecifyKind(request.ProductionDate, DateTimeKind.Unspecified);
                var now = DateTime.SpecifyKind(DateTime.UtcNow, DateTimeKind.Unspecified);

                var batch = new ProductionBatch
                {
                    QuotationID = quotation.QuotationID,
                    BatchNumber = batchNumber,
                    ProductID = product.ProductID,
                    TargetQuantity = request.TargetQuantity,
                    QuantityProduced = 0m,
                    QuantityDefective = 0m,
                    ProductionDate = productionDate,
                    Shift = shift,
                    LineNumber = string.IsNullOrWhiteSpace(lineNumber) ? null : lineNumber,
                    SupervisorID = request.SupervisorID,
                    EmployeeID = employeeId,
                    ProductionStatus = "Planned",
                    QualityStatus = "Pending",
                    RecordedAt = now,
                    StartTime = request.StartTime,
                    EndTime = request.EndTime
                };

                _context.ProductionBatches.Add(batch);
                await _context.SaveChangesAsync(ct);

                _context.CustomerOrderStatusHistories.Add(new CustomerOrderStatusHistory
                {
                    QuotationID = quotation.QuotationID,
                    BatchID = batch.BatchID,
                    Status = "Production Scheduled",
                    Description = $"Production batch {batch.BatchNumber} scheduled for {batch.TargetQuantity:0.##} units.",
                    CreatedAtUtc = now,
                    UpdatedByEmployeeID = employeeId,
                    IsVisibleToCustomer = true
                });
                await _context.SaveChangesAsync(ct);
                await transaction.CommitAsync(ct);

                return StatusCode(201, new
                {
                    success = true,
                    message = "Production batch created from the approved quotation.",
                    quotationID = quotation.QuotationID,
                    referenceNumber = quotation.ReferenceNumber,
                    batchID = batch.BatchID,
                    batchNumber = batch.BatchNumber,
                    productID = product.ProductID,
                    productCreated,
                    targetQuantity = batch.TargetQuantity,
                    remainingQuantity = remaining - batch.TargetQuantity,
                    productionStatus = batch.ProductionStatus
                });
            }
            catch (DbUpdateException)
            {
                await transaction.RollbackAsync(CancellationToken.None);
                return Conflict(new
                {
                    success = false,
                    message = "The batch or product could not be saved. Check for a duplicate product code or batch number, then retry."
                });
            }
            catch (Exception ex) when (ex.GetType().Name is "PostgresException" ||
                                       ex.InnerException?.GetType().Name == "PostgresException")
            {
                await transaction.RollbackAsync(CancellationToken.None);
                return Conflict(new
                {
                    success = false,
                    message = "A concurrent database change prevented scheduling. Refresh the quotation and retry."
                });
            }
        }
    }
}
