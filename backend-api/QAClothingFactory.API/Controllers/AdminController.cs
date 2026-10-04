using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Admin;
using QAClothingFactory.API.Models;
using QAClothingFactory.API.Services;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/admin")]
    [Authorize(Roles = "Admin")]
    public class AdminController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        private readonly SystemActivityService _systemActivityService;


        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public AdminController(
            ApplicationDbContext context,
            SystemActivityService systemActivityService
        )
        {
            _context = context;
            _systemActivityService = systemActivityService;
        }


        // ====================================================
        // ADMIN DASHBOARD
        // ====================================================

        [HttpGet("dashboard")]
        public async Task<ActionResult<AdminDashboardResponse>>
            GetDashboard()
        {
            // ================================================
            // EMPLOYEES
            // ================================================

            var totalEmployees =
                await _context
                    .Employees
                    .CountAsync();


            var activeEmployees =
                await _context
                    .Employees
                    .CountAsync(
                        employee =>
                            employee.EmployeeStatus == "Active"
                    );


            var pendingEmployees =
                await _context
                    .Employees
                    .CountAsync(
                        employee =>
                            employee.EmployeeStatus == "Pending"
                    );


            var inactiveEmployees =
                await _context
                    .Employees
                    .CountAsync(
                        employee =>
                            employee.EmployeeStatus == "Inactive"
                    );


            // ================================================
            // PRODUCTION
            // ================================================

            var totalProductionBatches =
                await _context
                    .ProductionBatches
                    .CountAsync();


            var pendingQualityChecks =
                await _context
                    .ProductionBatches
                    .CountAsync(
                        batch =>
                            batch.QualityStatus == "Pending"
                    );


            var approvedBatches =
                await _context
                    .ProductionBatches
                    .CountAsync(
                        batch =>
                            batch.QualityStatus == "Approved"
                    );


            var rejectedBatches =
                await _context
                    .ProductionBatches
                    .CountAsync(
                        batch =>
                            batch.QualityStatus == "Rejected"
                    );


            // ================================================
            // RAW MATERIAL INVENTORY
            // ================================================

            var totalRawMaterials =
                await _context
                    .RawMaterials
                    .CountAsync();


            var lowStockMaterials =
                await _context
                    .RawMaterials
                    .CountAsync(
                        material =>
                            material.CurrentStock <=
                            material.ReorderLevel
                    );


            // ================================================
            // FINISHED GOODS
            // ================================================

            var totalFinishedGoods =
                await _context
                    .FinishedGoods
                    .CountAsync();


            // ================================================
            // INVENTORY TRANSACTIONS
            // ================================================

            var totalInventoryTransactions =
                await _context
                    .InventoryTransactions
                    .CountAsync();


            // ================================================
            // SHIFT TEAMS
            // ================================================

            var totalShiftTeams =
                await _context
                    .ShiftTeams
                    .CountAsync();


            // ================================================
            // RESPONSE
            // ================================================

            var response =
                new AdminDashboardResponse
                {
                    Success = true,

                    TotalEmployees =
                        totalEmployees,

                    ActiveEmployees =
                        activeEmployees,

                    PendingEmployees =
                        pendingEmployees,

                    InactiveEmployees =
                        inactiveEmployees,

                    TotalProductionBatches =
                        totalProductionBatches,

                    PendingQualityChecks =
                        pendingQualityChecks,

                    ApprovedBatches =
                        approvedBatches,

                    RejectedBatches =
                        rejectedBatches,

                    TotalRawMaterials =
                        totalRawMaterials,

                    LowStockMaterials =
                        lowStockMaterials,

                    TotalFinishedGoods =
                        totalFinishedGoods,

                    TotalInventoryTransactions =
                        totalInventoryTransactions,

                    TotalShiftTeams =
                        totalShiftTeams
                };


            return Ok(response);
        }


        // ====================================================
        // GET ALL EMPLOYEES
        // ====================================================

        [HttpGet("employees")]
        public async Task<ActionResult<IEnumerable<AdminEmployeeResponse>>>
            GetEmployees()
        {
            var employees =
                await _context
                    .Employees
                    .AsNoTracking()
                    .OrderBy(
                        employee =>
                            employee.FullName
                    )
                    .Select(
                        employee =>
                            new AdminEmployeeResponse
                            {
                                EmployeeID =
                                    employee.EmployeeID,

                                FullName =
                                    employee.FullName,

                                EmailAddress =
                                    employee.EmailAddress,

                                Role =
                                    employee.Role,

                                EmployeeStatus =
                                    employee.EmployeeStatus,

                                CreatedAt =
                                    employee.CreatedAt,

                                CreatedBy =
                                    employee.CreatedBy
                            }
                    )
                    .ToListAsync();


            return Ok(employees);
        }


        // ====================================================
        // APPROVE EMPLOYEE
        // ====================================================
        //
        // Pending -> Active
        //
        // PUT /api/admin/employees/{employeeId}/approve
        //
        // ====================================================

        [HttpPut("employees/{employeeId:int}/approve")]
        public async Task<IActionResult> ApproveEmployee(
            int employeeId
        )
        {
            // ================================================
            // FIND EMPLOYEE
            // ================================================

            var employee =
                await _context
                    .Employees
                    .FirstOrDefaultAsync(
                        employee =>
                            employee.EmployeeID == employeeId
                    );


            // ================================================
            // EMPLOYEE NOT FOUND
            // ================================================

            if (employee == null)
            {
                return NotFound(
                    new
                    {
                        success = false,

                        message =
                            "Employee was not found."
                    }
                );
            }


            // ================================================
            // CHECK CURRENT STATUS
            // ================================================

            if (
                !string.Equals(
                    employee.EmployeeStatus,
                    "Pending",
                    StringComparison.OrdinalIgnoreCase
                )
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Only pending employees can be approved."
                    }
                );
            }


            // ================================================
            // APPROVE EMPLOYEE
            // ================================================

            employee.EmployeeStatus =
                "Active";


            // ================================================
            // SAVE TO DATABASE
            // ================================================

            await _context
                .SaveChangesAsync();


            // ================================================
            // SYSTEM ACTIVITY
            // ================================================

            await _systemActivityService
                .LogActivityAsync(
                    actionType:
                        "EMPLOYEE_APPROVED",

                    description:
                        $"Approved employee #{employee.EmployeeID} " +
                        $"({employee.FullName}).",

                    entityType:
                        "Employee",

                    entityId:
                        employee.EmployeeID
                );


            // ================================================
            // RESPONSE
            // ================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        $"{employee.FullName} has been approved successfully.",

                    employee =
                        CreateEmployeeResponse(
                            employee
                        )
                }
            );
        }


        // ====================================================
        // DEACTIVATE EMPLOYEE
        // ====================================================
        //
        // Active -> Inactive
        //
        // PUT /api/admin/employees/{employeeId}/deactivate
        //
        // ====================================================

        [HttpPut("employees/{employeeId:int}/deactivate")]
        public async Task<IActionResult> DeactivateEmployee(
            int employeeId
        )
        {
            // ================================================
            // FIND EMPLOYEE
            // ================================================

            var employee =
                await _context
                    .Employees
                    .FirstOrDefaultAsync(
                        employee =>
                            employee.EmployeeID == employeeId
                    );


            // ================================================
            // EMPLOYEE NOT FOUND
            // ================================================

            if (employee == null)
            {
                return NotFound(
                    new
                    {
                        success = false,

                        message =
                            "Employee was not found."
                    }
                );
            }


            // ================================================
            // PREVENT ADMIN FROM DEACTIVATING OWN ACCOUNT
            // ================================================

            var currentEmployeeIdClaim =
                User.FindFirst(
                    System.Security.Claims
                        .ClaimTypes
                        .NameIdentifier
                )
                ?.Value;


            if (
                int.TryParse(
                    currentEmployeeIdClaim,
                    out var currentEmployeeId
                ) &&
                currentEmployeeId == employeeId
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "You cannot deactivate your own account."
                    }
                );
            }


            // ================================================
            // CHECK CURRENT STATUS
            // ================================================

            if (
                !string.Equals(
                    employee.EmployeeStatus,
                    "Active",
                    StringComparison.OrdinalIgnoreCase
                )
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Only active employees can be deactivated."
                    }
                );
            }


            // ================================================
            // DEACTIVATE EMPLOYEE
            // ================================================

            employee.EmployeeStatus =
                "Inactive";


            // ================================================
            // SAVE TO DATABASE
            // ================================================

            await _context
                .SaveChangesAsync();


            // ================================================
            // SYSTEM ACTIVITY
            // ================================================

            await _systemActivityService
                .LogActivityAsync(
                    actionType:
                        "EMPLOYEE_DEACTIVATED",

                    description:
                        $"Deactivated employee #{employee.EmployeeID} " +
                        $"({employee.FullName}).",

                    entityType:
                        "Employee",

                    entityId:
                        employee.EmployeeID
                );


            // ================================================
            // RESPONSE
            // ================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        $"{employee.FullName} has been deactivated successfully.",

                    employee =
                        CreateEmployeeResponse(
                            employee
                        )
                }
            );
        }


        // ====================================================
        // REACTIVATE EMPLOYEE
        // ====================================================
        //
        // Inactive -> Active
        //
        // PUT /api/admin/employees/{employeeId}/reactivate
        //
        // ====================================================

        [HttpPut("employees/{employeeId:int}/reactivate")]
        public async Task<IActionResult> ReactivateEmployee(
            int employeeId
        )
        {
            // ================================================
            // FIND EMPLOYEE
            // ================================================

            var employee =
                await _context
                    .Employees
                    .FirstOrDefaultAsync(
                        employee =>
                            employee.EmployeeID == employeeId
                    );


            // ================================================
            // EMPLOYEE NOT FOUND
            // ================================================

            if (employee == null)
            {
                return NotFound(
                    new
                    {
                        success = false,

                        message =
                            "Employee was not found."
                    }
                );
            }


            // ================================================
            // CHECK CURRENT STATUS
            // ================================================

            if (
                !string.Equals(
                    employee.EmployeeStatus,
                    "Inactive",
                    StringComparison.OrdinalIgnoreCase
                )
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Only inactive employees can be reactivated."
                    }
                );
            }


            // ================================================
            // REACTIVATE EMPLOYEE
            // ================================================

            employee.EmployeeStatus =
                "Active";


            // ================================================
            // SAVE TO DATABASE
            // ================================================

            await _context
                .SaveChangesAsync();


            // ================================================
            // SYSTEM ACTIVITY
            // ================================================

            await _systemActivityService
                .LogActivityAsync(
                    actionType:
                        "EMPLOYEE_REACTIVATED",

                    description:
                        $"Reactivated employee #{employee.EmployeeID} " +
                        $"({employee.FullName}).",

                    entityType:
                        "Employee",

                    entityId:
                        employee.EmployeeID
                );


            // ================================================
            // RESPONSE
            // ================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        $"{employee.FullName} has been reactivated successfully.",

                    employee =
                        CreateEmployeeResponse(
                            employee
                        )
                }
            );
        }


        // ====================================================
        // CHANGE EMPLOYEE ROLE
        // ====================================================
        //
        // PUT /api/admin/employees/{employeeId}/role
        //
        // ====================================================

        [HttpPut("employees/{employeeId:int}/role")]
        public async Task<IActionResult> UpdateEmployeeRole(
            int employeeId,
            [FromBody] UpdateEmployeeRoleRequest request
        )
        {
            // ================================================
            // VALIDATE REQUEST
            // ================================================

            if (
                request == null ||
                string.IsNullOrWhiteSpace(
                    request.Role
                )
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "A role must be selected."
                    }
                );
            }


            // ================================================
            // VALID SYSTEM ROLES
            // ================================================

            var validRoles =
                new[]
                {
                    "Admin",
                    "Supervisor",
                    "QualityController",
                    "InventoryClerk",
                    "ProductionManager"
                };


            // ================================================
            // NORMALISE REQUESTED ROLE
            // ================================================

            var selectedRole =
                validRoles.FirstOrDefault(
                    role =>
                        string.Equals(
                            role,
                            request.Role.Trim(),
                            StringComparison.OrdinalIgnoreCase
                        )
                );


            // ================================================
            // INVALID ROLE
            // ================================================

            if (selectedRole == null)
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "The selected employee role is invalid."
                    }
                );
            }


            // ================================================
            // FIND EMPLOYEE
            // ================================================

            var employee =
                await _context
                    .Employees
                    .FirstOrDefaultAsync(
                        employee =>
                            employee.EmployeeID ==
                            employeeId
                    );


            // ================================================
            // EMPLOYEE NOT FOUND
            // ================================================

            if (employee == null)
            {
                return NotFound(
                    new
                    {
                        success = false,

                        message =
                            "Employee was not found."
                    }
                );
            }


            // ================================================
            // GET CURRENT ADMIN ID
            // ================================================

            var currentEmployeeIdClaim =
                User.FindFirst(
                    System.Security.Claims
                        .ClaimTypes
                        .NameIdentifier
                )
                ?.Value;


            var currentEmployeeId =
                0;


            int.TryParse(
                currentEmployeeIdClaim,
                out currentEmployeeId
            );


            // ================================================
            // PROTECT CURRENT ADMIN ACCOUNT
            // ================================================

            if (
                currentEmployeeId ==
                    employeeId &&
                !string.Equals(
                    selectedRole,
                    "Admin",
                    StringComparison.OrdinalIgnoreCase
                )
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "You cannot remove the Admin role from your own account."
                    }
                );
            }


            // ================================================
            // CHECK EXISTING ROLE
            // ================================================

            if (
                string.Equals(
                    employee.Role,
                    selectedRole,
                    StringComparison.OrdinalIgnoreCase
                )
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            $"{employee.FullName} already has the {selectedRole} role."
                    }
                );
            }


            // ================================================
            // SAVE PREVIOUS ROLE
            // ================================================

            var previousRole =
                employee.Role;


            // ================================================
            // UPDATE ROLE
            // ================================================

            employee.Role =
                selectedRole;


            // ================================================
            // SAVE TO DATABASE
            // ================================================

            await _context
                .SaveChangesAsync();


            // ================================================
            // SYSTEM ACTIVITY
            // ================================================

            await _systemActivityService
                .LogActivityAsync(
                    actionType:
                        "EMPLOYEE_ROLE_CHANGED",

                    description:
                        $"Changed employee #{employee.EmployeeID} " +
                        $"({employee.FullName}) role from " +
                        $"{previousRole} to {selectedRole}.",

                    entityType:
                        "Employee",

                    entityId:
                        employee.EmployeeID
                );


            // ================================================
            // RESPONSE
            // ================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        $"{employee.FullName}'s role has been changed from {previousRole} to {selectedRole} successfully.",

                    employee =
                        CreateEmployeeResponse(
                            employee
                        )
                }
            );
        }


        // ====================================================
        // CREATE SAFE EMPLOYEE RESPONSE
        // ====================================================

        private static AdminEmployeeResponse
            CreateEmployeeResponse(
                Employee employee
            )
        {
            return new AdminEmployeeResponse
            {
                EmployeeID =
                    employee.EmployeeID,

                FullName =
                    employee.FullName,

                EmailAddress =
                    employee.EmailAddress,

                Role =
                    employee.Role,

                EmployeeStatus =
                    employee.EmployeeStatus,

                CreatedAt =
                    employee.CreatedAt,

                CreatedBy =
                    employee.CreatedBy
            };
        }
    }
}