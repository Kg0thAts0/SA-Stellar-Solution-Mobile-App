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

                        TargetQuantity =
                            batch.TargetQuantity,

                        QuantityProduced =
                            batch.QuantityProduced,

                        AcceptedQuantity =
                            batch.QuantityProduced - (batch.QuantityDefective ?? 0),

                        ProgressPercentage =
                            batch.TargetQuantity > 0
                                ? (batch.QuantityProduced / batch.TargetQuantity) * 100
                                : 0,

                        ProductionStatus =
                            batch.ProductionStatus,

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

                        TargetQuantity =
                            productionBatch.TargetQuantity,

                        QuantityProduced =
                            productionBatch.QuantityProduced,

                        AcceptedQuantity =
                            productionBatch.QuantityProduced - (productionBatch.QuantityDefective ?? 0),

                        ProgressPercentage =
                            productionBatch.TargetQuantity > 0
                                ? (productionBatch.QuantityProduced / productionBatch.TargetQuantity) * 100
                                : 0,

                        ProductionStatus =
                            productionBatch.ProductionStatus,

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
        // GET PRODUCTION PROGRESS
        // ========================================================

        [HttpGet("progress")]
        public async Task<IActionResult> GetProductionProgress()
        {
            var batches =
                await (
                    from batch in _context.ProductionBatches
                    join product in _context.FinishedGoods
                        on batch.ProductID equals product.ProductID
                    join supervisorEmployee in _context.Employees
                        on batch.SupervisorID equals supervisorEmployee.EmployeeID
                        into supervisorGroup
                    from supervisor in supervisorGroup.DefaultIfEmpty()
                    orderby batch.ProductionDate descending, batch.BatchID descending
                    select new
                    {
                        batch.BatchID,
                        batch.BatchNumber,
                        batch.ProductID,
                        product.ProductCode,
                        product.ProductName,
                        batch.TargetQuantity,
                        batch.QuantityProduced,
                        QuantityDefective = batch.QuantityDefective ?? 0,
                        AcceptedQuantity =
                            batch.QuantityProduced - (batch.QuantityDefective ?? 0),
                        ProgressPercentage =
                            batch.TargetQuantity > 0
                                ? (batch.QuantityProduced / batch.TargetQuantity) * 100
                                : 0,
                        batch.ProductionStatus,
                        batch.SupervisorID,
                        SupervisorName =
                            supervisor != null ? supervisor.FullName : null,
                        batch.LineNumber,
                        batch.Shift,
                        batch.ProductionDate,
                        batch.StartTime,
                        batch.EndTime,
                        batch.QualityStatus
                    }
                )
                .AsNoTracking()
                .ToListAsync();

            var totalTarget = batches.Sum(batch => batch.TargetQuantity);
            var totalProduced = batches.Sum(batch => batch.QuantityProduced);
            var totalDefective = batches.Sum(batch => batch.QuantityDefective);
            var totalAccepted = batches.Sum(batch => batch.AcceptedQuantity);

            var overallProgressPercentage =
                totalTarget > 0
                    ? (totalProduced / totalTarget) * 100
                    : 0;

            var activeOrders =
                batches.Count(batch => batch.ProductionStatus == "In Progress");

            var completedOrders =
                batches.Count(batch => batch.ProductionStatus == "Completed");

            var plannedOrders =
                batches.Count(batch => batch.ProductionStatus == "Planned");

            var cancelledOrders =
                batches.Count(batch => batch.ProductionStatus == "Cancelled");

            return Ok(
                new
                {
                    success = true,
                    summary = new
                    {
                        totalOrders = batches.Count,
                        activeOrders,
                        completedOrders,
                        plannedOrders,
                        cancelledOrders,
                        totalTarget,
                        totalProduced,
                        totalDefective,
                        totalAccepted,
                        overallProgressPercentage
                    },
                    count = batches.Count,
                    batches
                }
            );
        }


        // ========================================================
        // GET PRODUCTION REPORTS
        // ========================================================

        [HttpGet("reports")]
        public async Task<IActionResult> GetProductionReports()
        {
            var batches =
                await (
                    from batch in _context.ProductionBatches
                    join product in _context.FinishedGoods
                        on batch.ProductID equals product.ProductID
                    join supervisorEmployee in _context.Employees
                        on batch.SupervisorID equals supervisorEmployee.EmployeeID
                        into supervisorGroup
                    from supervisor in supervisorGroup.DefaultIfEmpty()
                    orderby batch.ProductionDate descending, batch.BatchID descending
                    select new
                    {
                        batch.BatchID,
                        batch.BatchNumber,
                        batch.ProductID,
                        product.ProductCode,
                        product.ProductName,
                        batch.TargetQuantity,
                        batch.QuantityProduced,
                        QuantityDefective = batch.QuantityDefective ?? 0,
                        AcceptedQuantity =
                            batch.QuantityProduced - (batch.QuantityDefective ?? 0),
                        batch.ProductionStatus,
                        batch.ProductionDate,
                        batch.Shift,
                        batch.LineNumber,
                        batch.SupervisorID,
                        SupervisorName =
                            supervisor != null ? supervisor.FullName : null,
                        batch.StartTime,
                        batch.EndTime,
                        batch.QualityStatus
                    }
                )
                .AsNoTracking()
                .ToListAsync();

            var totalTarget =
                batches.Sum(batch => batch.TargetQuantity);

            var totalProduced =
                batches.Sum(batch => batch.QuantityProduced);

            var totalDefective =
                batches.Sum(batch => batch.QuantityDefective);

            var totalAccepted =
                batches.Sum(batch => batch.AcceptedQuantity);

            var targetAchievementPercentage =
                totalTarget > 0
                    ? (totalProduced / totalTarget) * 100
                    : 0;

            var defectRatePercentage =
                totalProduced > 0
                    ? (totalDefective / totalProduced) * 100
                    : 0;

            var productionByProduct =
                batches
                    .GroupBy(
                        batch =>
                            new
                            {
                                batch.ProductID,
                                batch.ProductCode,
                                batch.ProductName
                            }
                    )
                    .Select(
                        group =>
                            new
                            {
                                productID = group.Key.ProductID,
                                productCode = group.Key.ProductCode,
                                productName = group.Key.ProductName,
                                totalOrders = group.Count(),
                                totalTarget =
                                    group.Sum(batch => batch.TargetQuantity),
                                totalProduced =
                                    group.Sum(batch => batch.QuantityProduced),
                                totalAccepted =
                                    group.Sum(batch => batch.AcceptedQuantity),
                                totalDefective =
                                    group.Sum(batch => batch.QuantityDefective),
                                achievementPercentage =
                                    group.Sum(batch => batch.TargetQuantity) > 0
                                        ? (
                                            group.Sum(batch => batch.QuantityProduced) /
                                            group.Sum(batch => batch.TargetQuantity)
                                        ) * 100
                                        : 0
                            }
                    )
                    .OrderByDescending(item => item.totalProduced)
                    .ToList();

            var productionByShift =
                batches
                    .GroupBy(
                        batch =>
                            string.IsNullOrWhiteSpace(batch.Shift)
                                ? "Not Assigned"
                                : batch.Shift
                    )
                    .Select(
                        group =>
                            new
                            {
                                shift = group.Key,
                                totalOrders = group.Count(),
                                totalTarget =
                                    group.Sum(batch => batch.TargetQuantity),
                                totalProduced =
                                    group.Sum(batch => batch.QuantityProduced),
                                totalAccepted =
                                    group.Sum(batch => batch.AcceptedQuantity),
                                totalDefective =
                                    group.Sum(batch => batch.QuantityDefective)
                            }
                    )
                    .OrderByDescending(item => item.totalProduced)
                    .ToList();

            var productionByLine =
                batches
                    .GroupBy(
                        batch =>
                            string.IsNullOrWhiteSpace(batch.LineNumber)
                                ? "Not Assigned"
                                : batch.LineNumber
                    )
                    .Select(
                        group =>
                            new
                            {
                                lineNumber = group.Key,
                                totalOrders = group.Count(),
                                totalTarget =
                                    group.Sum(batch => batch.TargetQuantity),
                                totalProduced =
                                    group.Sum(batch => batch.QuantityProduced),
                                totalAccepted =
                                    group.Sum(batch => batch.AcceptedQuantity),
                                totalDefective =
                                    group.Sum(batch => batch.QuantityDefective)
                            }
                    )
                    .OrderByDescending(item => item.totalProduced)
                    .ToList();

            var supervisorPerformance =
                batches
                    .GroupBy(
                        batch =>
                            new
                            {
                                batch.SupervisorID,
                                SupervisorName =
                                    string.IsNullOrWhiteSpace(batch.SupervisorName)
                                        ? "Not Assigned"
                                        : batch.SupervisorName
                            }
                    )
                    .Select(
                        group =>
                            new
                            {
                                supervisorID = group.Key.SupervisorID,
                                supervisorName = group.Key.SupervisorName,
                                totalOrders = group.Count(),
                                completedOrders =
                                    group.Count(
                                        batch =>
                                            batch.ProductionStatus == "Completed"
                                    ),
                                totalTarget =
                                    group.Sum(batch => batch.TargetQuantity),
                                totalProduced =
                                    group.Sum(batch => batch.QuantityProduced),
                                totalAccepted =
                                    group.Sum(batch => batch.AcceptedQuantity),
                                totalDefective =
                                    group.Sum(batch => batch.QuantityDefective),
                                achievementPercentage =
                                    group.Sum(batch => batch.TargetQuantity) > 0
                                        ? (
                                            group.Sum(batch => batch.QuantityProduced) /
                                            group.Sum(batch => batch.TargetQuantity)
                                        ) * 100
                                        : 0,
                                defectRatePercentage =
                                    group.Sum(batch => batch.QuantityProduced) > 0
                                        ? (
                                            group.Sum(batch => batch.QuantityDefective) /
                                            group.Sum(batch => batch.QuantityProduced)
                                        ) * 100
                                        : 0
                            }
                    )
                    .OrderByDescending(item => item.totalProduced)
                    .ToList();

            var batchHistory =
                batches
                    .Select(
                        batch =>
                            new
                            {
                                batch.BatchID,
                                batch.BatchNumber,
                                batch.ProductID,
                                batch.ProductCode,
                                batch.ProductName,
                                batch.TargetQuantity,
                                batch.QuantityProduced,
                                batch.QuantityDefective,
                                batch.AcceptedQuantity,
                                progressPercentage =
                                    batch.TargetQuantity > 0
                                        ? (
                                            batch.QuantityProduced /
                                            batch.TargetQuantity
                                        ) * 100
                                        : 0,
                                batch.ProductionStatus,
                                batch.ProductionDate,
                                batch.Shift,
                                batch.LineNumber,
                                batch.SupervisorID,
                                batch.SupervisorName,
                                batch.StartTime,
                                batch.EndTime,
                                batch.QualityStatus
                            }
                    )
                    .ToList();

            return Ok(
                new
                {
                    success = true,

                    summary = new
                    {
                        totalOrders = batches.Count,
                        plannedOrders =
                            batches.Count(
                                batch => batch.ProductionStatus == "Planned"
                            ),
                        activeOrders =
                            batches.Count(
                                batch => batch.ProductionStatus == "In Progress"
                            ),
                        completedOrders =
                            batches.Count(
                                batch => batch.ProductionStatus == "Completed"
                            ),
                        cancelledOrders =
                            batches.Count(
                                batch => batch.ProductionStatus == "Cancelled"
                            ),
                        totalTarget,
                        totalProduced,
                        totalAccepted,
                        totalDefective,
                        targetAchievementPercentage,
                        defectRatePercentage
                    },

                    productionByProduct,
                    productionByShift,
                    productionByLine,
                    supervisorPerformance,

                    historyCount = batchHistory.Count,
                    batchHistory
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


            var productionStatus =
                request.ProductionStatus?.Trim()
                ?? "Planned";


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
            // VALIDATE PRODUCTION STATUS
            // ====================================================

            var allowedProductionStatuses =
                new[]
                {
                    "Planned",
                    "In Progress",
                    "Completed",
                    "Cancelled"
                };

            if (!allowedProductionStatuses.Contains(
                    productionStatus,
                    StringComparer.OrdinalIgnoreCase))
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Production status must be Planned, In Progress, Completed or Cancelled."
                    }
                );
            }

            productionStatus =
                allowedProductionStatuses.First(
                    allowed =>
                        allowed.Equals(
                            productionStatus,
                            StringComparison.OrdinalIgnoreCase
                        )
                );


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

                    TargetQuantity =
                        request.TargetQuantity,

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

                    ProductionStatus =
                        productionStatus,

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


        // ========================================================
        // UPDATE PRODUCTION BATCH
        // ========================================================

        [HttpPut("batches/{id:int}")]
        public async Task<IActionResult> UpdateProductionBatch(
            int id,
            [FromBody] UpdateProductionBatchRequest request
        )
        {
            // ====================================================
            // FIND PRODUCTION BATCH
            // ====================================================

            var productionBatch =
                await _context
                    .ProductionBatches
                    .FirstOrDefaultAsync(
                        batch =>
                            batch.BatchID == id
                    );


            if (productionBatch == null)
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


            // ====================================================
            // NORMALISE VALUES
            // ====================================================

            var batchNumber =
                request.BatchNumber?.Trim()
                ?? string.Empty;


            var shift =
                request.Shift?.Trim();


            var lineNumber =
                request.LineNumber?.Trim();


            var productionStatus =
                request.ProductionStatus?.Trim()
                ?? "Planned";


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
            // VALIDATE QUANTITY PRODUCED
            // ====================================================

            if (request.QuantityProduced < 0)
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Quantity produced cannot be negative."
                    }
                );
            }


            // ====================================================
            // VALIDATE PRODUCTION STATUS
            // ====================================================

            var allowedProductionStatuses =
                new[]
                {
                    "Planned",
                    "In Progress",
                    "Completed",
                    "Cancelled"
                };

            if (!allowedProductionStatuses.Contains(
                    productionStatus,
                    StringComparer.OrdinalIgnoreCase))
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Production status must be Planned, In Progress, Completed or Cancelled."
                    }
                );
            }

            productionStatus =
                allowedProductionStatuses.First(
                    allowed =>
                        allowed.Equals(
                            productionStatus,
                            StringComparison.OrdinalIgnoreCase
                        )
                );


            // ====================================================
            // VALIDATE DEFECTIVE QUANTITY
            // ====================================================

            if (
                request.QuantityDefective.HasValue &&
                request.QuantityDefective.Value < 0
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Defective quantity cannot be negative."
                    }
                );
            }


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
                            batch.BatchID != id &&
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
            // UPDATE PRODUCTION INFORMATION
            // ====================================================

            productionBatch.BatchNumber =
                batchNumber;


            productionBatch.ProductID =
                request.ProductID;


            productionBatch.TargetQuantity =
                request.TargetQuantity;


            productionBatch.QuantityProduced =
                request.QuantityProduced;


            productionBatch.QuantityDefective =
                request.QuantityDefective ?? 0;


            productionBatch.RawMaterialUsed =
                request.RawMaterialUsed;


            productionBatch.ProductionDate =
                request.ProductionDate;


            productionBatch.Shift =
                shift;


            productionBatch.LineNumber =
                string.IsNullOrWhiteSpace(lineNumber)
                    ? null
                    : lineNumber;


            productionBatch.SupervisorID =
                request.SupervisorID;


            productionBatch.ProductionStatus =
                productionStatus;


            productionBatch.StartTime =
                request.StartTime;


            productionBatch.EndTime =
                request.EndTime;


            /*
             * EmployeeID, RecordedAt and the quality-control
             * fields are deliberately not changed here.
             *
             * EmployeeID identifies who originally recorded
             * the batch.
             *
             * QualityStatus, QualityCheckedBy,
             * QualityCheckDate and QualityNotes belong to
             * the Quality Control workflow.
             */


            // ====================================================
            // SAVE CHANGES
            // ====================================================

            await _context
                .SaveChangesAsync();


            // ====================================================
            // SUCCESS
            // ====================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        "Production batch updated successfully.",

                    batchID =
                        productionBatch.BatchID,

                    batchNumber =
                        productionBatch.BatchNumber
                }
            );
        }
        // ========================================================
        // DELETE PRODUCTION BATCH
        // ========================================================

        [HttpDelete("batches/{id:int}")]
        public async Task<IActionResult> DeleteProductionBatch(
            int id
        )
        {
            // ====================================================
            // FIND PRODUCTION BATCH
            // ====================================================

            var productionBatch =
                await _context
                    .ProductionBatches
                    .FirstOrDefaultAsync(
                        batch =>
                            batch.BatchID == id
                    );


            // ====================================================
            // BATCH NOT FOUND
            // ====================================================

            if (productionBatch == null)
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


            // ====================================================
            // STORE VALUES BEFORE DELETE
            // ====================================================

            var deletedBatchID =
                productionBatch.BatchID;


            var deletedBatchNumber =
                productionBatch.BatchNumber;


            // ====================================================
            // DELETE PRODUCTION BATCH
            // ====================================================

            _context
                .ProductionBatches
                .Remove(
                    productionBatch
                );


            // ====================================================
            // SAVE CHANGES
            // ====================================================

            try
            {
                await _context
                    .SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                /*
                 * A production batch may eventually be referenced
                 * by another table such as quality inspections,
                 * stock movements or production history.
                 *
                 * In that situation we should not expose the
                 * database exception to the Android application.
                 */

                return Conflict(
                    new
                    {
                        success = false,

                        message =
                            "This production batch cannot be deleted because it is being used by another record."
                    }
                );
            }


            // ====================================================
            // SUCCESS
            // ====================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        "Production batch deleted successfully.",

                    batchID =
                        deletedBatchID,

                    batchNumber =
                        deletedBatchNumber
                }
            );
        }
    }

}