using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using System.Security.Claims;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/supervisor")]
    [Authorize(Roles = "Supervisor,Admin")]
    public class SupervisorController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public SupervisorController(ApplicationDbContext context)
        {
            _context = context;
        }

        // ========================================================
        // DASHBOARD
        // ========================================================

        [HttpGet("dashboard")]
        public async Task<IActionResult> GetDashboard()
        {
            var employeeResult = await GetAuthenticatedEmployeeAsync();

            if (employeeResult.EmployeeID == null)
            {
                return employeeResult.ErrorResult!;
            }

            var employeeId = employeeResult.EmployeeID.Value;

            var assignedBatches = await _context.ProductionBatches
                .AsNoTracking()
                .Where(batch => batch.SupervisorID == employeeId)
                .ToListAsync();

            var shiftTeams = await _context.ShiftTeams
                .AsNoTracking()
                .Where(team => team.SupervisorID == employeeId)
                .ToListAsync();

            var activeBatches = assignedBatches.Count(
                batch => batch.ProductionStatus == "Planned" ||
                         batch.ProductionStatus == "In Progress"
            );

            var completedBatches = assignedBatches.Count(
                batch => batch.ProductionStatus == "Completed"
            );

            var totalTargetQuantity = assignedBatches.Sum(
                batch => batch.TargetQuantity
            );

            var totalQuantityProduced = assignedBatches.Sum(
                batch => batch.QuantityProduced
            );

            var totalQuantityDefective = assignedBatches.Sum(
                batch => batch.QuantityDefective ?? 0
            );

            var acceptedQuantity =
                totalQuantityProduced - totalQuantityDefective;

            var overallProgressPercentage =
                totalTargetQuantity > 0
                    ? (totalQuantityProduced / totalTargetQuantity) * 100
                    : 0;

            return Ok(new
            {
                success = true,
                totalAssignedBatches = assignedBatches.Count,
                activeBatches,
                completedBatches,
                totalShiftTeams = shiftTeams.Count,
                totalTargetQuantity,
                totalQuantityProduced,
                totalQuantityDefective,
                acceptedQuantity,
                overallProgressPercentage
            });
        }

        // ========================================================
        // ASSIGNED WORK
        // ========================================================

        [HttpGet("assigned-work")]
        public async Task<IActionResult> GetAssignedWork()
        {
            var employeeResult = await GetAuthenticatedEmployeeAsync();

            if (employeeResult.EmployeeID == null)
            {
                return employeeResult.ErrorResult!;
            }

            var employeeId = employeeResult.EmployeeID.Value;

            var batches = await (
                from batch in _context.ProductionBatches
                join product in _context.FinishedGoods
                    on batch.ProductID equals product.ProductID
                where batch.SupervisorID == employeeId
                orderby batch.ProductionDate descending,
                        batch.BatchID descending
                select new
                {
                    batchID = batch.BatchID,
                    batchNumber = batch.BatchNumber,

                    productID = batch.ProductID,
                    productCode = product.ProductCode,
                    productName = product.ProductName,

                    targetQuantity = batch.TargetQuantity,
                    quantityProduced = batch.QuantityProduced,
                    quantityDefective = batch.QuantityDefective ?? 0,

                    acceptedQuantity =
                        batch.QuantityProduced -
                        (batch.QuantityDefective ?? 0),

                    progressPercentage =
                        batch.TargetQuantity > 0
                            ? (batch.QuantityProduced /
                               batch.TargetQuantity) * 100
                            : 0,

                    productionStatus = batch.ProductionStatus,

                    productionDate = batch.ProductionDate,
                    shift = batch.Shift,
                    lineNumber = batch.LineNumber,

                    startTime = batch.StartTime,
                    endTime = batch.EndTime,

                    rawMaterialUsed = batch.RawMaterialUsed,

                    qualityStatus = batch.QualityStatus,
                    qualityCheckedBy = batch.QualityCheckedBy,
                    qualityCheckDate = batch.QualityCheckDate,
                    qualityNotes = batch.QualityNotes,

                    recordedAt = batch.RecordedAt
                }
            )
            .AsNoTracking()
            .ToListAsync();

            return Ok(new
            {
                success = true,
                count = batches.Count,
                batches
            });
        }

        // ========================================================
        // ASSIGNED WORK DETAILS
        // ========================================================

        [HttpGet("assigned-work/{batchId:int}")]
        public async Task<IActionResult> GetAssignedWorkById(int batchId)
        {
            var employeeResult = await GetAuthenticatedEmployeeAsync();

            if (employeeResult.EmployeeID == null)
            {
                return employeeResult.ErrorResult!;
            }

            var employeeId = employeeResult.EmployeeID.Value;

            var batch = await (
                from productionBatch in _context.ProductionBatches
                join product in _context.FinishedGoods
                    on productionBatch.ProductID equals product.ProductID
                where productionBatch.BatchID == batchId &&
                      productionBatch.SupervisorID == employeeId
                select new
                {
                    batchID = productionBatch.BatchID,
                    batchNumber = productionBatch.BatchNumber,

                    productID = productionBatch.ProductID,
                    productCode = product.ProductCode,
                    productName = product.ProductName,

                    targetQuantity = productionBatch.TargetQuantity,
                    quantityProduced = productionBatch.QuantityProduced,
                    quantityDefective =
                        productionBatch.QuantityDefective ?? 0,

                    acceptedQuantity =
                        productionBatch.QuantityProduced -
                        (productionBatch.QuantityDefective ?? 0),

                    progressPercentage =
                        productionBatch.TargetQuantity > 0
                            ? (productionBatch.QuantityProduced /
                               productionBatch.TargetQuantity) * 100
                            : 0,

                    rawMaterialUsed = productionBatch.RawMaterialUsed,

                    productionDate = productionBatch.ProductionDate,
                    shift = productionBatch.Shift,
                    lineNumber = productionBatch.LineNumber,

                    startTime = productionBatch.StartTime,
                    endTime = productionBatch.EndTime,

                    productionStatus =
                        productionBatch.ProductionStatus,

                    qualityStatus = productionBatch.QualityStatus,
                    qualityCheckedBy =
                        productionBatch.QualityCheckedBy,
                    qualityCheckDate =
                        productionBatch.QualityCheckDate,
                    qualityNotes =
                        productionBatch.QualityNotes,

                    recordedAt = productionBatch.RecordedAt
                }
            )
            .AsNoTracking()
            .FirstOrDefaultAsync();

            if (batch == null)
            {
                return NotFound(new
                {
                    success = false,
                    message =
                        "The production batch was not found or is not assigned to this supervisor."
                });
            }

            return Ok(new
            {
                success = true,
                batch
            });
        }

        // ========================================================
        // TEAM PROGRESS
        // ========================================================

        [HttpGet("team-progress")]
        public async Task<IActionResult> GetTeamProgress()
        {
            var employeeResult = await GetAuthenticatedEmployeeAsync();

            if (employeeResult.EmployeeID == null)
            {
                return employeeResult.ErrorResult!;
            }

            var employeeId = employeeResult.EmployeeID.Value;

            var batches = await (
                from batch in _context.ProductionBatches
                join product in _context.FinishedGoods
                    on batch.ProductID equals product.ProductID
                where batch.SupervisorID == employeeId
                orderby batch.ProductionDate descending,
                        batch.BatchID descending
                select new
                {
                    batchID = batch.BatchID,
                    batchNumber = batch.BatchNumber,

                    productCode = product.ProductCode,
                    productName = product.ProductName,

                    targetQuantity = batch.TargetQuantity,
                    quantityProduced = batch.QuantityProduced,
                    quantityDefective = batch.QuantityDefective ?? 0,

                    acceptedQuantity =
                        batch.QuantityProduced -
                        (batch.QuantityDefective ?? 0),

                    progressPercentage =
                        batch.TargetQuantity > 0
                            ? (batch.QuantityProduced /
                               batch.TargetQuantity) * 100
                            : 0,

                    productionStatus = batch.ProductionStatus,
                    qualityStatus = batch.QualityStatus,

                    productionDate = batch.ProductionDate,
                    shift = batch.Shift,
                    lineNumber = batch.LineNumber,

                    startTime = batch.StartTime,
                    endTime = batch.EndTime
                }
            )
            .AsNoTracking()
            .ToListAsync();

            var totalTargetQuantity = batches.Sum(
                batch => batch.targetQuantity
            );

            var totalQuantityProduced = batches.Sum(
                batch => batch.quantityProduced
            );

            var totalQuantityDefective = batches.Sum(
                batch => batch.quantityDefective
            );

            var totalAcceptedQuantity =
                totalQuantityProduced - totalQuantityDefective;

            var overallProgressPercentage =
                totalTargetQuantity > 0
                    ? (totalQuantityProduced /
                       totalTargetQuantity) * 100
                    : 0;

            return Ok(new
            {
                success = true,

                summary = new
                {
                    totalBatches = batches.Count,
                    totalTargetQuantity,
                    totalQuantityProduced,
                    totalQuantityDefective,
                    totalAcceptedQuantity,
                    overallProgressPercentage
                },

                batches
            });
        }

        // ========================================================
        // SHIFT TEAMS
        // ========================================================

        [HttpGet("shift-teams")]
        public async Task<IActionResult> GetShiftTeams()
        {
            var employeeResult = await GetAuthenticatedEmployeeAsync();

            if (employeeResult.EmployeeID == null)
            {
                return employeeResult.ErrorResult!;
            }

            var employeeId = employeeResult.EmployeeID.Value;

            var teams = await _context.ShiftTeams
                .AsNoTracking()
                .Where(team => team.SupervisorID == employeeId)
                .OrderByDescending(team => team.ShiftDate)
                .ThenByDescending(team => team.ShiftTeamID)
                .Select(team => new
                {
                    shiftTeamID = team.ShiftTeamID,
                    shiftDate = team.ShiftDate,
                    shift = team.Shift,
                    supervisorID = team.SupervisorID,
                    lineNumber = team.LineNumber,
                    totalEmployees = team.TotalEmployees,
                    notes = team.Notes,
                    createdAt = team.CreatedAt
                })
                .ToListAsync();

            return Ok(new
            {
                success = true,
                count = teams.Count,
                teams
            });
        }

        // ========================================================
        // DAILY UPDATES
        // ========================================================

        [HttpGet("daily-updates")]
        public async Task<IActionResult> GetDailyUpdates()
        {
            var employeeResult = await GetAuthenticatedEmployeeAsync();

            if (employeeResult.EmployeeID == null)
            {
                return employeeResult.ErrorResult!;
            }

            var employeeId = employeeResult.EmployeeID.Value;

            var batches = await (
                from batch in _context.ProductionBatches
                join product in _context.FinishedGoods
                    on batch.ProductID equals product.ProductID
                where batch.SupervisorID == employeeId
                orderby batch.ProductionDate descending,
                        batch.BatchID descending
                select new
                {
                    batchID = batch.BatchID,
                    batchNumber = batch.BatchNumber,

                    productCode = product.ProductCode,
                    productName = product.ProductName,

                    productionDate = batch.ProductionDate,
                    shift = batch.Shift,
                    lineNumber = batch.LineNumber,

                    targetQuantity = batch.TargetQuantity,
                    quantityProduced = batch.QuantityProduced,
                    quantityDefective = batch.QuantityDefective ?? 0,

                    acceptedQuantity =
                        batch.QuantityProduced -
                        (batch.QuantityDefective ?? 0),

                    progressPercentage =
                        batch.TargetQuantity > 0
                            ? (batch.QuantityProduced /
                               batch.TargetQuantity) * 100
                            : 0,

                    productionStatus = batch.ProductionStatus,

                    startTime = batch.StartTime,
                    endTime = batch.EndTime,

                    qualityStatus = batch.QualityStatus
                }
            )
            .AsNoTracking()
            .ToListAsync();

            return Ok(new
            {
                success = true,
                count = batches.Count,
                updates = batches
            });
        }

        // ========================================================
        // UPDATE DAILY PRODUCTION PROGRESS
        // ========================================================

        [HttpPut("daily-updates/{batchId:int}")]
        public async Task<IActionResult> UpdateDailyProgress(
            int batchId,
            [FromBody] SupervisorDailyUpdateRequest request
        )
        {
            var employeeResult = await GetAuthenticatedEmployeeAsync();

            if (employeeResult.EmployeeID == null)
            {
                return employeeResult.ErrorResult!;
            }

            var employeeId = employeeResult.EmployeeID.Value;

            var batch = await _context.ProductionBatches
                .FirstOrDefaultAsync(
                    productionBatch =>
                        productionBatch.BatchID == batchId &&
                        productionBatch.SupervisorID == employeeId
                );

            if (batch == null)
            {
                return NotFound(new
                {
                    success = false,
                    message =
                        "The production batch was not found or is not assigned to this supervisor."
                });
            }

            if (request.QuantityProduced < 0)
            {
                return BadRequest(new
                {
                    success = false,
                    message =
                        "Quantity produced cannot be negative."
                });
            }

            if (request.QuantityDefective < 0)
            {
                return BadRequest(new
                {
                    success = false,
                    message =
                        "Quantity defective cannot be negative."
                });
            }

            if (request.QuantityDefective >
                request.QuantityProduced)
            {
                return BadRequest(new
                {
                    success = false,
                    message =
                        "Quantity defective cannot be greater than quantity produced."
                });
            }

            if (batch.TargetQuantity > 0 &&
                request.QuantityProduced > batch.TargetQuantity)
            {
                return BadRequest(new
                {
                    success = false,
                    message =
                        "Quantity produced cannot be greater than the target quantity."
                });
            }

            var productionStatus =
                request.ProductionStatus?.Trim();

            var validStatuses = new[]
            {
                "Planned",
                "In Progress",
                "Completed",
                "Cancelled"
            };

            if (string.IsNullOrWhiteSpace(productionStatus) ||
                !validStatuses.Contains(
                    productionStatus,
                    StringComparer.OrdinalIgnoreCase
                ))
            {
                return BadRequest(new
                {
                    success = false,
                    message =
                        "Production status must be Planned, In Progress, Completed or Cancelled."
                });
            }

            productionStatus = validStatuses.First(
                status => status.Equals(
                    productionStatus,
                    StringComparison.OrdinalIgnoreCase
                )
            );

            if (request.StartTime.HasValue &&
                request.EndTime.HasValue &&
                request.EndTime.Value < request.StartTime.Value)
            {
                return BadRequest(new
                {
                    success = false,
                    message =
                        "End time cannot be earlier than start time."
                });
            }

            batch.QuantityProduced = request.QuantityProduced;
            batch.QuantityDefective = request.QuantityDefective;
            batch.ProductionStatus = productionStatus;
            batch.StartTime = request.StartTime;
            batch.EndTime = request.EndTime;

            await _context.SaveChangesAsync();

            var acceptedQuantity =
                batch.QuantityProduced -
                (batch.QuantityDefective ?? 0);

            var progressPercentage =
                batch.TargetQuantity > 0
                    ? (batch.QuantityProduced /
                       batch.TargetQuantity) * 100
                    : 0;

            return Ok(new
            {
                success = true,
                message =
                    "Daily production progress updated successfully.",

                batch = new
                {
                    batchID = batch.BatchID,
                    batchNumber = batch.BatchNumber,
                    targetQuantity = batch.TargetQuantity,
                    quantityProduced = batch.QuantityProduced,
                    quantityDefective =
                        batch.QuantityDefective ?? 0,
                    acceptedQuantity,
                    progressPercentage,
                    productionStatus =
                        batch.ProductionStatus,
                    productionDate =
                        batch.ProductionDate,
                    shift = batch.Shift,
                    lineNumber = batch.LineNumber,
                    startTime = batch.StartTime,
                    endTime = batch.EndTime
                }
            });
        }

        // ========================================================
        // AUTHENTICATED EMPLOYEE
        // ========================================================

        private async Task<AuthenticatedEmployeeResult>
            GetAuthenticatedEmployeeAsync()
        {
            var employeeIdValue =
                User.FindFirstValue(
                    ClaimTypes.NameIdentifier
                );

            if (!int.TryParse(
                    employeeIdValue,
                    out var employeeId
                ))
            {
                return new AuthenticatedEmployeeResult
                {
                    ErrorResult = Unauthorized(new
                    {
                        success = false,
                        message =
                            "Unable to identify the authenticated employee."
                    })
                };
            }

            var employee = await _context.Employees
                .AsNoTracking()
                .FirstOrDefaultAsync(
                    item =>
                        item.EmployeeID == employeeId &&
                        item.EmployeeStatus == "Active" &&
                        (
                            item.Role == "Supervisor" ||
                            item.Role == "Admin"
                        )
                );

            if (employee == null)
            {
                return new AuthenticatedEmployeeResult
                {
                    ErrorResult = Unauthorized(new
                    {
                        success = false,
                        message =
                            "The authenticated employee is not an active Supervisor or Admin."
                    })
                };
            }

            return new AuthenticatedEmployeeResult
            {
                EmployeeID = employee.EmployeeID
            };
        }

        private sealed class AuthenticatedEmployeeResult
        {
            public int? EmployeeID { get; init; }

            public IActionResult? ErrorResult { get; init; }
        }
    }

    // ============================================================
    // REQUEST DTO
    // ============================================================

    public class SupervisorDailyUpdateRequest
    {
        public decimal QuantityProduced { get; set; }

        public decimal QuantityDefective { get; set; }

        public string ProductionStatus { get; set; } =
            "In Progress";

        public TimeSpan? StartTime { get; set; }

        public TimeSpan? EndTime { get; set; }
    }
}
