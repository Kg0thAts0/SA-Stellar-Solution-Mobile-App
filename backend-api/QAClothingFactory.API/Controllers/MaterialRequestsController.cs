using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Models;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/material-requests")]
    [Authorize(Roles = "ProductionManager,InventoryClerk,Admin")]
    public class MaterialRequestsController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public MaterialRequestsController(ApplicationDbContext context)
        {
            _context = context;
        }

        // ============================================================
        // READ-ONLY MATERIAL CATALOG FOR PRODUCTION MANAGERS
        // GET /api/material-requests/catalog
        // ============================================================
        [HttpGet("catalog")]
        [Authorize(Roles = "ProductionManager,Admin")]
        public async Task<IActionResult> GetMaterialCatalog()
        {
            var employee = await GetCurrentEmployeeAsync("ProductionManager", "Admin");
            if (employee == null)
                return Unauthorized(new { success = false, message = "An active Production Manager or Admin account is required." });

            var materials = await _context.RawMaterials
                .AsNoTracking()
                .OrderBy(m => m.MaterialName)
                .Select(m => new
                {
                    m.MaterialID,
                    m.MaterialCode,
                    m.MaterialName,
                    m.Unit,
                    m.CurrentStock,
                    StockStatus = m.CurrentStock <= 0
                        ? "Out of Stock"
                        : m.CurrentStock <= m.ReorderLevel
                            ? "Low Stock"
                            : "In Stock"
                })
                .ToListAsync();

            return Ok(new { success = true, count = materials.Count, materials });
        }

        // POST /api/material-requests
        [HttpPost]
        [Authorize(Roles = "ProductionManager,Admin")]
        public async Task<IActionResult> CreateRequest([FromBody] CreateMaterialRequestDto request)
        {
            var employee = await GetCurrentEmployeeAsync("ProductionManager", "Admin");
            if (employee == null)
                return Unauthorized(new { success = false, message = "An active Production Manager or Admin account is required." });

            if (request.BatchID <= 0 || request.Items == null || request.Items.Count == 0)
                return BadRequest(new { success = false, message = "A valid production batch and at least one material are required." });

            if (request.Items.Any(i => i.MaterialID <= 0 || i.QuantityRequested <= 0 ||
                                       decimal.Round(i.QuantityRequested, 2) != i.QuantityRequested))
                return BadRequest(new { success = false, message = "Each material needs a valid ID and a positive quantity with at most two decimal places." });

            if (request.Items.Select(i => i.MaterialID).Distinct().Count() != request.Items.Count)
                return BadRequest(new { success = false, message = "Each raw material can appear only once in a request." });

            var batch = await _context.ProductionBatches.AsNoTracking()
                .FirstOrDefaultAsync(b => b.BatchID == request.BatchID);
            if (batch == null)
                return NotFound(new { success = false, message = "Production batch was not found." });

            if (string.Equals(batch.ProductionStatus, "Cancelled", StringComparison.OrdinalIgnoreCase) ||
                string.Equals(batch.ProductionStatus, "Completed", StringComparison.OrdinalIgnoreCase))
                return BadRequest(new { success = false, message = "Cannot request materials for a completed or cancelled production batch." });

            var materialIds = request.Items.Select(i => i.MaterialID).ToArray();
            var existingIds = await _context.RawMaterials.AsNoTracking()
                .Where(m => materialIds.Contains(m.MaterialID))
                .Select(m => m.MaterialID).ToListAsync();
            if (existingIds.Count != materialIds.Length)
                return BadRequest(new { success = false, message = "One or more raw materials do not exist." });

            var materialRequest = new MaterialRequest
            {
                BatchID = request.BatchID,
                RequestedBy = employee.EmployeeID,
                RequestedAt = DateTime.UtcNow,
                Status = "Pending",
                Notes = Clean(request.Notes),
                Items = request.Items.Select(i => new MaterialRequestItem
                {
                    MaterialID = i.MaterialID,
                    QuantityRequested = i.QuantityRequested,
                    QuantityIssued = 0m,
                    Notes = Clean(i.Notes)
                }).ToList()
            };

            _context.MaterialRequests.Add(materialRequest);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetRequest), new { id = materialRequest.RequestID },
                new { success = true, message = "Material request submitted to Inventory Clerk.", requestID = materialRequest.RequestID });
        }

        // GET /api/material-requests?status=Pending&batchId=1
        [HttpGet]
        public async Task<IActionResult> GetRequests([FromQuery] string? status = null, [FromQuery] int? batchId = null)
        {
            var employee = await GetCurrentEmployeeAsync("ProductionManager", "InventoryClerk", "Admin");
            if (employee == null)
                return Unauthorized(new { success = false, message = "An active authorized employee account is required." });

            var query = _context.MaterialRequests.AsNoTracking().AsQueryable();
            if (employee.Role == "ProductionManager")
                query = query.Where(r => r.RequestedBy == employee.EmployeeID);
            if (!string.IsNullOrWhiteSpace(status))
                query = query.Where(r => r.Status == status.Trim());
            if (batchId.HasValue)
                query = query.Where(r => r.BatchID == batchId.Value);

            var requests = await query.OrderByDescending(r => r.RequestedAt)
                .Select(r => new
                {
                    r.RequestID,
                    r.BatchID,
                    r.RequestedBy,
                    r.RequestedAt,
                    r.Status,
                    r.Notes,
                    Items = r.Items.Select(i => new
                    {
                        i.RequestItemID,
                        i.MaterialID,
                        i.RawMaterial.MaterialCode,
                        i.RawMaterial.MaterialName,
                        i.RawMaterial.Unit,
                        i.QuantityRequested,
                        i.QuantityIssued,
                        QuantityRemaining = i.QuantityRequested - i.QuantityIssued,
                        i.Notes
                    }).ToList()
                }).ToListAsync();

            return Ok(new { success = true, count = requests.Count, requests });
        }

        // GET /api/material-requests/123
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetRequest(int id)
        {
            var employee = await GetCurrentEmployeeAsync("ProductionManager", "InventoryClerk", "Admin");
            if (employee == null)
                return Unauthorized(new { success = false, message = "An active authorized employee account is required." });

            var query = _context.MaterialRequests.AsNoTracking().Where(r => r.RequestID == id);
            if (employee.Role == "ProductionManager")
                query = query.Where(r => r.RequestedBy == employee.EmployeeID);

            var materialRequest = await query.Select(r => new
            {
                r.RequestID,
                r.BatchID,
                r.RequestedBy,
                r.RequestedAt,
                r.Status,
                r.Notes,
                Items = r.Items.Select(i => new
                {
                    i.RequestItemID,
                    i.MaterialID,
                    i.RawMaterial.MaterialCode,
                    i.RawMaterial.MaterialName,
                    i.RawMaterial.Unit,
                    i.RawMaterial.CurrentStock,
                    i.QuantityRequested,
                    i.QuantityIssued,
                    QuantityRemaining = i.QuantityRequested - i.QuantityIssued,
                    i.Notes
                }).ToList()
            }).FirstOrDefaultAsync();

            if (materialRequest == null)
                return NotFound(new { success = false, message = "Material request was not found." });

            return Ok(new { success = true, request = materialRequest });
        }

        // POST /api/material-requests/123/issue
        // Body: { "items": [ { "requestItemID": 1, "quantity": 5.00 } ] }
        [HttpPost("{id:int}/issue")]
        [Authorize(Roles = "InventoryClerk,Admin")]
        public async Task<IActionResult> IssueMaterials(int id, [FromBody] IssueMaterialRequestDto request)
        {
            var employee = await GetCurrentEmployeeAsync("InventoryClerk", "Admin");
            if (employee == null)
                return Unauthorized(new { success = false, message = "An active Inventory Clerk or Admin account is required." });

            if (request.Items == null || request.Items.Count == 0 ||
                request.Items.Any(i => i.RequestItemID <= 0 || i.Quantity <= 0 ||
                                       decimal.Round(i.Quantity, 2) != i.Quantity) ||
                request.Items.Select(i => i.RequestItemID).Distinct().Count() != request.Items.Count)
                return BadRequest(new { success = false, message = "Supply unique request item IDs and positive quantities with at most two decimal places." });

            await using var transaction = await _context.Database.BeginTransactionAsync();
            try
            {
                // Lock the parent request so two issue operations for the same request
                // cannot both process its remaining quantities concurrently.
                var lockedIds = await _context.Database.SqlQueryRaw<int>(
                    "SELECT `RequestID` AS `Value` FROM `material_request` WHERE `RequestID` = {0} FOR UPDATE", id)
                    .ToListAsync();
                if (lockedIds.Count == 0)
                    return NotFound(new { success = false, message = "Material request was not found." });

                var materialRequest = await _context.MaterialRequests
                    .Include(r => r.Items)
                    .FirstAsync(r => r.RequestID == id);

                if (materialRequest.Status == "Cancelled" || materialRequest.Status == "Fulfilled")
                    return BadRequest(new { success = false, message = "This request cannot be issued because it is cancelled or already fulfilled." });

                var batchStatus = await _context.ProductionBatches.AsNoTracking()
                    .Where(b => b.BatchID == materialRequest.BatchID)
                    .Select(b => b.ProductionStatus)
                    .FirstOrDefaultAsync();
                if (string.Equals(batchStatus, "Cancelled", StringComparison.OrdinalIgnoreCase) ||
                    string.Equals(batchStatus, "Completed", StringComparison.OrdinalIgnoreCase))
                    return BadRequest(new { success = false, message = "The production batch is completed or cancelled." });

                var now = DateTime.UtcNow;
                foreach (var issued in request.Items.OrderBy(i => i.RequestItemID))
                {
                    var item = materialRequest.Items.FirstOrDefault(i => i.RequestItemID == issued.RequestItemID);
                    if (item == null)
                        return BadRequest(new { success = false, message = $"Item {issued.RequestItemID} does not belong to this request." });

                    if (issued.Quantity > item.QuantityRequested - item.QuantityIssued)
                        return BadRequest(new { success = false, message = $"Issued quantity exceeds remaining quantity for item {item.RequestItemID}." });

                    // Atomic stock deduction: concurrent transactions cannot drive stock negative.
                    var updated = await _context.Database.ExecuteSqlInterpolatedAsync($@"
                        UPDATE `raw_material`
                        SET `CurrentStock` = `CurrentStock` - {issued.Quantity},
                            `LastUpdated` = {now}, `UpdatedBy` = {employee.EmployeeID}
                        WHERE `MaterialID` = {item.MaterialID}
                          AND `CurrentStock` >= {issued.Quantity}");
                    if (updated != 1)
                        return Conflict(new { success = false, message = $"Insufficient stock for material {item.MaterialID}; no materials were issued." });

                    var unitCost = await _context.RawMaterials.AsNoTracking()
                        .Where(m => m.MaterialID == item.MaterialID)
                        .Select(m => m.UnitCost).SingleAsync();

                    item.QuantityIssued += issued.Quantity;
                    _context.InventoryTransactions.Add(new InventoryTransaction
                    {
                        MaterialID = item.MaterialID,
                        ProductID = null,
                        TransactionType = "Issue",
                        Quantity = issued.Quantity,
                        UnitCost = unitCost,
                        ReferenceNumber = $"MR-{materialRequest.RequestID}",
                        Notes = $"Issued for production batch {materialRequest.BatchID}; request item {item.RequestItemID}.",
                        TransactionDate = now,
                        PerformedBy = employee.EmployeeID
                    });
                }

                materialRequest.Status = materialRequest.Items.All(i => i.QuantityIssued >= i.QuantityRequested)
                    ? "Fulfilled" : "Partially Issued";

                await _context.SaveChangesAsync();
                await transaction.CommitAsync();
                return Ok(new
                {
                    success = true,
                    message = "Materials issued and inventory stock updated successfully.",
                    requestID = materialRequest.RequestID,
                    status = materialRequest.Status,
                    issuedBy = employee.EmployeeID
                });
            }
            catch
            {
                await transaction.RollbackAsync();
                throw;
            }
        }

        private async Task<Employee?> GetCurrentEmployeeAsync(params string[] allowedRoles)
        {
            if (!int.TryParse(User.FindFirstValue(ClaimTypes.NameIdentifier), out var employeeId))
                return null;

            var employee = await _context.Employees.AsNoTracking()
                .FirstOrDefaultAsync(e => e.EmployeeID == employeeId && e.EmployeeStatus == "Active");

            return employee != null && employee.Role != null && allowedRoles.Contains(employee.Role)
                ? employee : null;
        }

        private static string? Clean(string? value) =>
            string.IsNullOrWhiteSpace(value) ? null : value.Trim();
    }

    public class CreateMaterialRequestDto
    {
        public int BatchID { get; set; }
        public string? Notes { get; set; }
        public List<CreateMaterialRequestItemDto> Items { get; set; } = new();
    }

    public class CreateMaterialRequestItemDto
    {
        public int MaterialID { get; set; }
        public decimal QuantityRequested { get; set; }
        public string? Notes { get; set; }
    }

    public class IssueMaterialRequestDto
    {
        public List<IssueMaterialRequestItemDto> Items { get; set; } = new();
    }

    public class IssueMaterialRequestItemDto
    {
        public int RequestItemID { get; set; }
        public decimal Quantity { get; set; }
    }
}