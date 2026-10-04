using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Production;
using QAClothingFactory.API.Models;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/production")]
    [Authorize(Roles = "ProductionManager,Admin")]
    public class ProductionController : ControllerBase
    {
        private readonly ApplicationDbContext _context;


        // ========================================================
        // CONSTRUCTOR
        // ========================================================

        public ProductionController(
            ApplicationDbContext context
        )
        {
            _context = context;
        }


        // ========================================================
        // GET ALL PRODUCTION BATCHES
        // ========================================================

        [HttpGet("batches")]
        public async Task<IActionResult> GetProductionBatches()
        {
            var batches =
                await (
                    from batch in _context.ProductionBatches

                    join product in _context.FinishedGoods
                        on batch.ProductID equals product.ProductID

                    join supervisorEmployee in _context.Employees
                        on batch.SupervisorID equals
                        supervisorEmployee.EmployeeID
                        into supervisorGroup

                    from supervisor in
                        supervisorGroup.DefaultIfEmpty()

                    join recordedEmployee in _context.Employees
                        on batch.EmployeeID equals
                        recordedEmployee.EmployeeID
                        into recordedEmployeeGroup

                    from recordedBy in
                        recordedEmployeeGroup.DefaultIfEmpty()

                    join qualityEmployee in _context.Employees
                        on batch.QualityCheckedBy equals
                        qualityEmployee.EmployeeID
                        into qualityEmployeeGroup

                    from qualityCheckedBy in
                        qualityEmployeeGroup.DefaultIfEmpty()

                    orderby batch.ProductionDate descending,
                        batch.BatchID descending

                    select new ProductionBatchResponse
                    {
                        BatchID =
                            batch.BatchID,

                        BatchNumber =
                            batch.BatchNumber,

                        ProductID =
                            batch.ProductID,

                        ProductCode =
                            product.ProductCode,

                        ProductName =
                            product.ProductName,

                        QuantityProduced =
                            batch.QuantityProduced,

                        QuantityDefective =
                            batch.QuantityDefective,

                        RawMaterialUsed =
                            batch.RawMaterialUsed,

                        ProductionDate =
                            batch.ProductionDate,

                        Shift =
                            batch.Shift,

                        LineNumber =
                            batch.LineNumber,

                        StartTime =
                            batch.StartTime,

                        EndTime =
                            batch.EndTime,

                        SupervisorID =
                            batch.SupervisorID,

                        SupervisorName =
                            supervisor != null
                                ? supervisor.FullName
                                : null,

                        EmployeeID =
                            batch.EmployeeID,

                        EmployeeName =
                            recordedBy != null
                                ? recordedBy.FullName
                                : null,

                        QualityStatus =
                            batch.QualityStatus,

                        QualityCheckedBy =
                            batch.QualityCheckedBy,

                        QualityCheckedByName =
                            qualityCheckedBy != null
                                ? qualityCheckedBy.FullName
                                : null,

                        QualityCheckDate =
                            batch.QualityCheckDate,

                        QualityNotes =
                            batch.QualityNotes,

                        RecordedAt =
                            batch.RecordedAt
                    }
                )
                .AsNoTracking()
                .ToListAsync();


            return Ok(
                new
                {
                    success = true,

                    count =
                        batches.Count,

                    batches
                }
            );
        }


        // ========================================================
        // GET PRODUCTION BATCH BY ID
        // ========================================================

        [HttpGet("batches/{id:int}")]
        public async Task<IActionResult> GetProductionBatch(
            int id
        )
        {
            var batch =
                await (
                    from productionBatch in
                        _context.ProductionBatches

                    join product in _context.FinishedGoods
                        on productionBatch.ProductID equals
                        product.ProductID

                    join supervisorEmployee in _context.Employees
                        on productionBatch.SupervisorID equals
                        supervisorEmployee.EmployeeID
                        into supervisorGroup

                    from supervisor in
                        supervisorGroup.DefaultIfEmpty()

                    join recordedEmployee in _context.Employees
                        on productionBatch.EmployeeID equals
                        recordedEmployee.EmployeeID
                        into recordedEmployeeGroup

                    from recordedBy in
                        recordedEmployeeGroup.DefaultIfEmpty()

                    join qualityEmployee in _context.Employees
                        on productionBatch.QualityCheckedBy equals
                        qualityEmployee.EmployeeID
                        into qualityEmployeeGroup

                    from qualityCheckedBy in
                        qualityEmployeeGroup.DefaultIfEmpty()

                    where productionBatch.BatchID == id

                    select new ProductionBatchResponse
                    {
                        BatchID =
                            productionBatch.BatchID,

                        BatchNumber =
                            productionBatch.BatchNumber,

                        ProductID =
                            productionBatch.ProductID,

                        ProductCode =
                            product.ProductCode,

                        ProductName =
                            product.ProductName,

                        QuantityProduced =
                            productionBatch.QuantityProduced,

                        QuantityDefective =
                            productionBatch.QuantityDefective,

                        RawMaterialUsed =
                            productionBatch.RawMaterialUsed,

                        ProductionDate =
                            productionBatch.ProductionDate,

                        Shift =
                            productionBatch.Shift,

                        LineNumber =
                            productionBatch.LineNumber,

                        StartTime =
                            productionBatch.StartTime,

                        EndTime =
                            productionBatch.EndTime,

                        SupervisorID =
                            productionBatch.SupervisorID,

                        SupervisorName =
                            supervisor != null
                                ? supervisor.FullName
                                : null,

                        EmployeeID =
                            productionBatch.EmployeeID,

                        EmployeeName =
                            recordedBy != null
                                ? recordedBy.FullName
                                : null,

                        QualityStatus =
                            productionBatch.QualityStatus,

                        QualityCheckedBy =
                            productionBatch.QualityCheckedBy,

                        QualityCheckedByName =
                            qualityCheckedBy != null
                                ? qualityCheckedBy.FullName
                                : null,

                        QualityCheckDate =
                            productionBatch.QualityCheckDate,

                        QualityNotes =
                            productionBatch.QualityNotes,

                        RecordedAt =
                            productionBatch.RecordedAt
                    }
                )
                .AsNoTracking()
                .FirstOrDefaultAsync();


            if (batch == null)
            {
                return NotFound(
                    new
                    {
                        success = false,

                        message =
                            "Production batch was not found."
                    }
                );
            }


            return Ok(
                new
                {
                    success = true,

                    batch
                }
            );
        }


        // ========================================================
        // GET PRODUCTS
        // ========================================================

        [HttpGet("products")]
        public async Task<IActionResult> GetProducts()
        {
            var products =
                await _context
                    .FinishedGoods
                    .AsNoTracking()
                    .OrderBy(
                        product =>
                            product.ProductName
                    )
                    .Select(
                        product =>
                            new ProductionProductResponse
                            {
                                ProductID =
                                    product.ProductID,

                                ProductCode =
                                    product.ProductCode,

                                ProductName =
                                    product.ProductName,

                                Category =
                                    product.Category,

                                Unit =
                                    product.Unit,

                                CurrentStock =
                                    product.CurrentStock
                            }
                    )
                    .ToListAsync();


            return Ok(
                new
                {
                    success = true,

                    count =
                        products.Count,

                    products
                }
            );
        }


        // ========================================================
        // GET ACTIVE SUPERVISORS
        // ========================================================

        [HttpGet("supervisors")]
        public async Task<IActionResult> GetSupervisors()
        {
            var supervisors =
                await _context
                    .Employees
                    .AsNoTracking()
                    .Where(
                        employee =>
                            employee.Role == "Supervisor" &&
                            employee.EmployeeStatus == "Active"
                    )
                    .OrderBy(
                        employee =>
                            employee.FullName
                    )
                    .Select(
                        employee =>
                            new ProductionSupervisorResponse
                            {
                                EmployeeID =
                                    employee.EmployeeID,

                                FullName =
                                    employee.FullName,

                                EmailAddress =
                                    employee.EmailAddress
                            }
                    )
                    .ToListAsync();


            return Ok(
                new
                {
                    success = true,

                    count =
                        supervisors.Count,

                    supervisors
                }
            );
        }


        // ========================================================
        // CREATE PRODUCTION BATCH
        // ========================================================

        [HttpPost("batches")]
        public async Task<IActionResult> CreateProductionBatch(
            [FromBody] CreateProductionBatchRequest request
        )
        {
            // ====================================================
            // NORMALISE VALUES
            // ====================================================

            var batchNumber =
                request.BatchNumber.Trim();


            var shift =
                request.Shift?.Trim();


            var lineNumber =
                request.LineNumber?.Trim();


            // ====================================================
            // VALIDATE BATCH NUMBER
            // ====================================================

            if (string.IsNullOrWhiteSpace(batchNumber))
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Batch number is required."
                    }
                );
            }


            // ====================================================
            // VALIDATE SHIFT
            // ====================================================

            var allowedShifts =
                new[]
                {
                    "Morning",
                    "Afternoon",
                    "Night"
                };


            if (
                !string.IsNullOrWhiteSpace(shift) &&
                !allowedShifts.Contains(
                    shift,
                    StringComparer.OrdinalIgnoreCase
                )
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Shift must be Morning, Afternoon or Night."
                    }
                );
            }


            if (!string.IsNullOrWhiteSpace(shift))
            {
                shift =
                    allowedShifts.First(
                        allowed =>
                            allowed.Equals(
                                shift,
                                StringComparison.OrdinalIgnoreCase
                            )
                    );
            }


            // ====================================================
            // VALIDATE DEFECTIVE QUANTITY
            // ====================================================

            if (
                request.QuantityDefective.HasValue &&
                request.QuantityDefective.Value >
                request.QuantityProduced
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Defective quantity cannot exceed quantity produced."
                    }
                );
            }


            // ====================================================
            // VALIDATE START / END TIME
            // ====================================================

            if (
                request.StartTime.HasValue &&
                request.EndTime.HasValue &&
                request.EndTime.Value <
                request.StartTime.Value
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "End time cannot be earlier than start time."
                    }
                );
            }


            // ====================================================
            // CHECK DUPLICATE BATCH NUMBER
            // ====================================================

            var batchNumberExists =
                await _context
                    .ProductionBatches
                    .AnyAsync(
                        batch =>
                            batch.BatchNumber.ToLower() ==
                            batchNumber.ToLower()
                    );


            if (batchNumberExists)
            {
                return Conflict(
                    new
                    {
                        success = false,

                        message =
                            "A production batch with this batch number already exists."
                    }
                );
            }


            // ====================================================
            // CHECK PRODUCT
            // ====================================================

            var productExists =
                await _context
                    .FinishedGoods
                    .AnyAsync(
                        product =>
                            product.ProductID ==
                            request.ProductID
                    );


            if (!productExists)
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "The selected product does not exist."
                    }
                );
            }


            // ====================================================
            // CHECK SUPERVISOR
            // ====================================================

            if (request.SupervisorID.HasValue)
            {
                var supervisorExists =
                    await _context
                        .Employees
                        .AnyAsync(
                            employee =>
                                employee.EmployeeID ==
                                    request.SupervisorID.Value &&
                                employee.Role ==
                                    "Supervisor" &&
                                employee.EmployeeStatus ==
                                    "Active"
                        );


                if (!supervisorExists)
                {
                    return BadRequest(
                        new
                        {
                            success = false,

                            message =
                                "The selected supervisor is not an active Supervisor."
                        }
                    );
                }
            }


            // ====================================================
            // GET LOGGED-IN EMPLOYEE ID
            // ====================================================

            var employeeIdValue =
                User.FindFirstValue(
                    ClaimTypes.NameIdentifier
                );


            if (
                !int.TryParse(
                    employeeIdValue,
                    out var employeeId
                )
            )
            {
                return Unauthorized(
                    new
                    {
                        success = false,

                        message =
                            "Unable to identify the authenticated employee."
                    }
                );
            }


            // ====================================================
            // CREATE PRODUCTION BATCH
            // ====================================================

            var productionBatch =
                new ProductionBatch
                {
                    BatchNumber =
                        batchNumber,

                    ProductID =
                        request.ProductID,

                    QuantityProduced =
                        request.QuantityProduced,

                    QuantityDefective =
                        request.QuantityDefective ?? 0,

                    RawMaterialUsed =
                        request.RawMaterialUsed,

                    ProductionDate =
                        request.ProductionDate,

                    Shift =
                        shift,

                    LineNumber =
                        string.IsNullOrWhiteSpace(lineNumber)
                            ? null
                            : lineNumber,

                    SupervisorID =
                        request.SupervisorID,

                    EmployeeID =
                        employeeId,

                    QualityStatus =
                        "Pending",

                    QualityCheckedBy =
                        null,

                    QualityCheckDate =
                        null,

                    QualityNotes =
                        null,

                    RecordedAt =
                        DateTime.UtcNow,

                    StartTime =
                        request.StartTime,

                    EndTime =
                        request.EndTime
                };


            _context
                .ProductionBatches
                .Add(
                    productionBatch
                );


            await _context
                .SaveChangesAsync();


            // ====================================================
            // SUCCESS
            // ====================================================

            return CreatedAtAction(
                nameof(GetProductionBatch),

                new
                {
                    id =
                        productionBatch.BatchID
                },

                new
                {
                    success = true,

                    message =
                        "Production batch created successfully.",

                    batchID =
                        productionBatch.BatchID,

                    batchNumber =
                        productionBatch.BatchNumber
                }
            );
        }
    }
}