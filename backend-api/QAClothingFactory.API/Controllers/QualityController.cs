using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/quality")]
    [Authorize(Roles = "QualityController,Admin")]
    public class QualityController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public QualityController(ApplicationDbContext context)
        {
            _context = context;
        }


        // ============================================================
        // QUALITY CONTROL DASHBOARD
        // ============================================================

        [HttpGet("dashboard")]
        public async Task<IActionResult> GetDashboard()
        {
            var batches = await _context
                .ProductionBatches
                .AsNoTracking()
                .ToListAsync();

            var totalBatches = batches.Count;

            var pendingInspections = batches.Count(
                batch =>
                    string.Equals(
                        batch.QualityStatus,
                        "Pending",
                        StringComparison.OrdinalIgnoreCase
                    )
            );

            var approvedBatches = batches.Count(
                batch =>
                    string.Equals(
                        batch.QualityStatus,
                        "Approved",
                        StringComparison.OrdinalIgnoreCase
                    )
            );

            var rejectedBatches = batches.Count(
                batch =>
                    string.Equals(
                        batch.QualityStatus,
                        "Rejected",
                        StringComparison.OrdinalIgnoreCase
                    )
            );

            var totalProduced =
                batches.Sum(batch => batch.QuantityProduced);

            var totalDefective =
                batches.Sum(batch => batch.QuantityDefective ?? 0);

            var totalAccepted =
                totalProduced - totalDefective;

            var defectRatePercentage =
                totalProduced > 0
                    ? (totalDefective / totalProduced) * 100
                    : 0;

            var approvalRatePercentage =
                approvedBatches + rejectedBatches > 0
                    ? (
                        (decimal)approvedBatches /
                        (approvedBatches + rejectedBatches)
                      ) * 100
                    : 0;

            return Ok(
                new
                {
                    success = true,

                    dashboard = new
                    {
                        totalBatches,
                        pendingInspections,
                        approvedBatches,
                        rejectedBatches,
                        totalProduced,
                        totalAccepted,
                        totalDefective,
                        defectRatePercentage,
                        approvalRatePercentage
                    }
                }
            );
        }


        // ============================================================
        // GET ALL QUALITY INSPECTIONS
        // ============================================================

        [HttpGet("inspections")]
        public async Task<IActionResult> GetInspections(
            [FromQuery] string? status = null
        )
        {
            var query =
                from batch in _context.ProductionBatches

                join product in _context.FinishedGoods
                    on batch.ProductID equals product.ProductID

                join supervisorEmployee in _context.Employees
                    on batch.SupervisorID equals supervisorEmployee.EmployeeID
                    into supervisorGroup

                from supervisor in supervisorGroup.DefaultIfEmpty()

                join qualityEmployee in _context.Employees
                    on batch.QualityCheckedBy equals qualityEmployee.EmployeeID
                    into qualityEmployeeGroup

                from qualityInspector in
                    qualityEmployeeGroup.DefaultIfEmpty()

                select new
                {
                    batch.BatchID,
                    batch.BatchNumber,

                    batch.ProductID,
                    product.ProductCode,
                    product.ProductName,

                    batch.TargetQuantity,
                    batch.QuantityProduced,

                    QuantityDefective =
                        batch.QuantityDefective ?? 0,

                    AcceptedQuantity =
                        batch.QuantityProduced -
                        (batch.QuantityDefective ?? 0),

                    batch.ProductionStatus,
                    batch.ProductionDate,
                    batch.Shift,
                    batch.LineNumber,

                    batch.SupervisorID,

                    SupervisorName =
                        supervisor != null
                            ? supervisor.FullName
                            : null,

                    QualityStatus =
                        batch.QualityStatus ?? "Pending",

                    batch.QualityCheckedBy,

                    QualityCheckedByName =
                        qualityInspector != null
                            ? qualityInspector.FullName
                            : null,

                    batch.QualityCheckDate,
                    batch.QualityNotes,
                    batch.RecordedAt
                };

            if (!string.IsNullOrWhiteSpace(status))
            {
                var normalisedStatus = status.Trim();

                var allowedStatuses =
                    new[]
                    {
                        "Pending",
                        "Approved",
                        "Rejected"
                    };

                if (!allowedStatuses.Contains(
                        normalisedStatus,
                        StringComparer.OrdinalIgnoreCase))
                {
                    return BadRequest(
                        new
                        {
                            success = false,

                            message =
                                "Quality status must be Pending, Approved or Rejected."
                        }
                    );
                }

                var selectedStatus =
                    allowedStatuses.First(
                        allowed =>
                            allowed.Equals(
                                normalisedStatus,
                                StringComparison.OrdinalIgnoreCase
                            )
                    );

                query = query.Where(
                    batch =>
                        batch.QualityStatus == selectedStatus
                );
            }

            var inspections =
                await query
                    .AsNoTracking()
                    .OrderByDescending(
                        batch => batch.ProductionDate
                    )
                    .ThenByDescending(
                        batch => batch.BatchID
                    )
                    .ToListAsync();

            return Ok(
                new
                {
                    success = true,
                    count = inspections.Count,
                    inspections
                }
            );
        }


        // ============================================================
        // GET PENDING QUALITY INSPECTIONS
        // ============================================================

        [HttpGet("inspections/pending")]
        public async Task<IActionResult> GetPendingInspections()
        {
            var inspections =
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

                    where
                        batch.QualityStatus == null ||
                        batch.QualityStatus == "Pending"

                    orderby
                        batch.ProductionDate descending,
                        batch.BatchID descending

                    select new
                    {
                        batch.BatchID,
                        batch.BatchNumber,

                        batch.ProductID,
                        product.ProductCode,
                        product.ProductName,

                        batch.TargetQuantity,
                        batch.QuantityProduced,

                        QuantityDefective =
                            batch.QuantityDefective ?? 0,

                        AcceptedQuantity =
                            batch.QuantityProduced -
                            (batch.QuantityDefective ?? 0),

                        batch.ProductionStatus,
                        batch.ProductionDate,
                        batch.Shift,
                        batch.LineNumber,

                        batch.SupervisorID,

                        SupervisorName =
                            supervisor != null
                                ? supervisor.FullName
                                : null,

                        QualityStatus =
                            batch.QualityStatus ?? "Pending",

                        batch.StartTime,
                        batch.EndTime,
                        batch.RecordedAt
                    }
                )
                .AsNoTracking()
                .ToListAsync();

            return Ok(
                new
                {
                    success = true,
                    count = inspections.Count,
                    inspections
                }
            );
        }


        // ============================================================
        // GET QUALITY INSPECTION BY BATCH ID
        // ============================================================

        [HttpGet("inspections/{batchId:int}")]
        public async Task<IActionResult> GetInspection(
            int batchId
        )
        {
            var inspection =
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

                    from qualityInspector in
                        qualityEmployeeGroup.DefaultIfEmpty()

                    where batch.BatchID == batchId

                    select new
                    {
                        batch.BatchID,
                        batch.BatchNumber,

                        batch.ProductID,
                        product.ProductCode,
                        product.ProductName,

                        batch.TargetQuantity,
                        batch.QuantityProduced,

                        QuantityDefective =
                            batch.QuantityDefective ?? 0,

                        AcceptedQuantity =
                            batch.QuantityProduced -
                            (batch.QuantityDefective ?? 0),

                        batch.RawMaterialUsed,
                        batch.ProductionDate,
                        batch.Shift,
                        batch.LineNumber,

                        batch.SupervisorID,

                        SupervisorName =
                            supervisor != null
                                ? supervisor.FullName
                                : null,

                        batch.EmployeeID,

                        RecordedByName =
                            recordedBy != null
                                ? recordedBy.FullName
                                : null,

                        batch.ProductionStatus,

                        QualityStatus =
                            batch.QualityStatus ?? "Pending",

                        batch.QualityCheckedBy,

                        QualityCheckedByName =
                            qualityInspector != null
                                ? qualityInspector.FullName
                                : null,

                        batch.QualityCheckDate,
                        batch.QualityNotes,

                        batch.RecordedAt,
                        batch.StartTime,
                        batch.EndTime
                    }
                )
                .AsNoTracking()
                .FirstOrDefaultAsync();

            if (inspection == null)
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
                    inspection
                }
            );
        }


        // ============================================================
        // RECORD / UPDATE QUALITY INSPECTION
        // ============================================================

        [HttpPut("inspections/{batchId:int}")]
        public async Task<IActionResult> UpdateInspection(
            int batchId,
            [FromBody] QualityInspectionRequest request
        )
        {
            var productionBatch =
                await _context
                    .ProductionBatches
                    .FirstOrDefaultAsync(
                        batch =>
                            batch.BatchID == batchId
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


            // ========================================================
            // VALIDATE QUALITY STATUS
            // ========================================================

            var qualityStatus =
                request.QualityStatus?.Trim()
                ?? string.Empty;

            var allowedStatuses =
                new[]
                {
                    "Approved",
                    "Rejected"
                };

            if (!allowedStatuses.Contains(
                    qualityStatus,
                    StringComparer.OrdinalIgnoreCase))
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Quality status must be Approved or Rejected."
                    }
                );
            }

            qualityStatus =
                allowedStatuses.First(
                    allowed =>
                        allowed.Equals(
                            qualityStatus,
                            StringComparison.OrdinalIgnoreCase
                        )
                );


            // ========================================================
            // VALIDATE QUALITY NOTES
            // ========================================================

            var qualityNotes =
                request.QualityNotes?.Trim();

            if (
                qualityStatus == "Rejected" &&
                string.IsNullOrWhiteSpace(qualityNotes)
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Quality notes are required when rejecting a production batch."
                    }
                );
            }


            // ========================================================
            // GET LOGGED-IN QUALITY CONTROLLER
            // ========================================================

            var employeeIdValue =
                User.FindFirstValue(
                    ClaimTypes.NameIdentifier
                );

            if (!int.TryParse(
                    employeeIdValue,
                    out var employeeId))
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


            // ========================================================
            // VERIFY EMPLOYEE
            // ========================================================

            var qualityControllerExists =
                await _context
                    .Employees
                    .AnyAsync(
                        employee =>
                            employee.EmployeeID == employeeId &&
                            (
                                employee.Role ==
                                    "QualityController" ||
                                employee.Role ==
                                    "Admin"
                            ) &&
                            employee.EmployeeStatus ==
                                "Active"
                    );

            if (!qualityControllerExists)
            {
                return Unauthorized(
                    new
                    {
                        success = false,

                        message =
                            "The authenticated employee is not an active Quality Controller."
                    }
                );
            }


            // ========================================================
            // UPDATE QUALITY INFORMATION
            // ========================================================

            productionBatch.QualityStatus =
                qualityStatus;

            productionBatch.QualityCheckedBy =
                employeeId;

            productionBatch.QualityCheckDate =
                DateTime.UtcNow;

            productionBatch.QualityNotes =
                string.IsNullOrWhiteSpace(qualityNotes)
                    ? null
                    : qualityNotes;


            // ========================================================
            // SAVE
            // ========================================================

            await _context
                .SaveChangesAsync();


            return Ok(
                new
                {
                    success = true,

                    message =
                        qualityStatus == "Approved"
                            ? "Production batch approved successfully."
                            : "Production batch rejected successfully.",

                    inspection = new
                    {
                        productionBatch.BatchID,
                        productionBatch.BatchNumber,

                        QualityStatus =
                            productionBatch.QualityStatus,

                        productionBatch.QualityCheckedBy,
                        productionBatch.QualityCheckDate,
                        productionBatch.QualityNotes
                    }
                }
            );
        }


        // ============================================================
        // DEFECTS
        // ============================================================

        [HttpGet("defects")]
        public async Task<IActionResult> GetDefects()
        {
            var defects =
                await (
                    from batch in _context.ProductionBatches

                    join product in _context.FinishedGoods
                        on batch.ProductID equals product.ProductID

                    where
                        (batch.QuantityDefective ?? 0) > 0

                    orderby
                        batch.ProductionDate descending,
                        batch.BatchID descending

                    select new
                    {
                        batch.BatchID,
                        batch.BatchNumber,

                        batch.ProductID,
                        product.ProductCode,
                        product.ProductName,

                        batch.QuantityProduced,

                        QuantityDefective =
                            batch.QuantityDefective ?? 0,

                        AcceptedQuantity =
                            batch.QuantityProduced -
                            (batch.QuantityDefective ?? 0),

                        DefectRatePercentage =
                            batch.QuantityProduced > 0
                                ? (
                                    (batch.QuantityDefective ?? 0) /
                                    batch.QuantityProduced
                                  ) * 100
                                : 0,

                        QualityStatus =
                            batch.QualityStatus ?? "Pending",

                        batch.QualityNotes,
                        batch.ProductionDate,
                        batch.Shift,
                        batch.LineNumber
                    }
                )
                .AsNoTracking()
                .ToListAsync();

            var totalDefective =
                defects.Sum(
                    defect =>
                        defect.QuantityDefective
                );

            return Ok(
                new
                {
                    success = true,
                    count = defects.Count,
                    totalDefective,
                    defects
                }
            );
        }


        // ============================================================
        // QUALITY REPORTS
        // ============================================================

        [HttpGet("reports")]
        public async Task<IActionResult> GetReports()
        {
            var batches =
                await (
                    from batch in _context.ProductionBatches

                    join product in _context.FinishedGoods
                        on batch.ProductID equals product.ProductID

                    join qualityEmployee in _context.Employees
                        on batch.QualityCheckedBy equals
                        qualityEmployee.EmployeeID
                        into qualityEmployeeGroup

                    from qualityInspector in
                        qualityEmployeeGroup.DefaultIfEmpty()

                    orderby
                        batch.ProductionDate descending,
                        batch.BatchID descending

                    select new
                    {
                        batch.BatchID,
                        batch.BatchNumber,

                        batch.ProductID,
                        product.ProductCode,
                        product.ProductName,

                        batch.QuantityProduced,

                        QuantityDefective =
                            batch.QuantityDefective ?? 0,

                        AcceptedQuantity =
                            batch.QuantityProduced -
                            (batch.QuantityDefective ?? 0),

                        QualityStatus =
                            batch.QualityStatus ?? "Pending",

                        batch.QualityCheckedBy,

                        QualityCheckedByName =
                            qualityInspector != null
                                ? qualityInspector.FullName
                                : null,

                        batch.QualityCheckDate,
                        batch.QualityNotes,
                        batch.ProductionDate,
                        batch.Shift,
                        batch.LineNumber
                    }
                )
                .AsNoTracking()
                .ToListAsync();

            var totalBatches =
                batches.Count;

            var pending =
                batches.Count(
                    batch =>
                        batch.QualityStatus == "Pending"
                );

            var approved =
                batches.Count(
                    batch =>
                        batch.QualityStatus == "Approved"
                );

            var rejected =
                batches.Count(
                    batch =>
                        batch.QualityStatus == "Rejected"
                );

            var totalProduced =
                batches.Sum(
                    batch =>
                        batch.QuantityProduced
                );

            var totalDefective =
                batches.Sum(
                    batch =>
                        batch.QuantityDefective
                );

            var totalAccepted =
                batches.Sum(
                    batch =>
                        batch.AcceptedQuantity
                );

            var defectRatePercentage =
                totalProduced > 0
                    ? (totalDefective / totalProduced) * 100
                    : 0;

            var inspectedCount =
                approved + rejected;

            var approvalRatePercentage =
                inspectedCount > 0
                    ? (
                        (decimal)approved /
                        inspectedCount
                      ) * 100
                    : 0;


            // ========================================================
            // REPORT BY PRODUCT
            // ========================================================

            var qualityByProduct =
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
                                productID =
                                    group.Key.ProductID,

                                productCode =
                                    group.Key.ProductCode,

                                productName =
                                    group.Key.ProductName,

                                totalBatches =
                                    group.Count(),

                                approvedBatches =
                                    group.Count(
                                        batch =>
                                            batch.QualityStatus ==
                                            "Approved"
                                    ),

                                rejectedBatches =
                                    group.Count(
                                        batch =>
                                            batch.QualityStatus ==
                                            "Rejected"
                                    ),

                                pendingBatches =
                                    group.Count(
                                        batch =>
                                            batch.QualityStatus ==
                                            "Pending"
                                    ),

                                totalProduced =
                                    group.Sum(
                                        batch =>
                                            batch.QuantityProduced
                                    ),

                                totalAccepted =
                                    group.Sum(
                                        batch =>
                                            batch.AcceptedQuantity
                                    ),

                                totalDefective =
                                    group.Sum(
                                        batch =>
                                            batch.QuantityDefective
                                    ),

                                defectRatePercentage =
                                    group.Sum(
                                        batch =>
                                            batch.QuantityProduced
                                    ) > 0
                                        ? (
                                            group.Sum(
                                                batch =>
                                                    batch.QuantityDefective
                                            ) /
                                            group.Sum(
                                                batch =>
                                                    batch.QuantityProduced
                                            )
                                          ) * 100
                                        : 0
                            }
                    )
                    .OrderByDescending(
                        item =>
                            item.totalProduced
                    )
                    .ToList();


            // ========================================================
            // INSPECTION HISTORY
            // ========================================================

            var inspectionHistory =
                batches
                    .Where(
                        batch =>
                            batch.QualityStatus != "Pending"
                    )
                    .OrderByDescending(
                        batch =>
                            batch.QualityCheckDate
                    )
                    .ToList();


            return Ok(
                new
                {
                    success = true,

                    summary = new
                    {
                        totalBatches,
                        pendingInspections = pending,
                        approvedBatches = approved,
                        rejectedBatches = rejected,

                        totalProduced,
                        totalAccepted,
                        totalDefective,

                        defectRatePercentage,
                        approvalRatePercentage
                    },

                    qualityByProduct,

                    historyCount =
                        inspectionHistory.Count,

                    inspectionHistory
                }
            );
        }
    }


    // ================================================================
    // QUALITY INSPECTION REQUEST
    // ================================================================

    public class QualityInspectionRequest
    {
        public string QualityStatus { get; set; } =
            string.Empty;

        public string? QualityNotes { get; set; }
    }
}