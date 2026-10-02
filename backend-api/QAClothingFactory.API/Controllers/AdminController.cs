using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Admin;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/admin")]
    [Authorize(Roles = "Admin")]
    public class AdminController : ControllerBase
    {
        private readonly ApplicationDbContext _context;


        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public AdminController(
            ApplicationDbContext context
        )
        {
            _context = context;
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
                await _context.Employees.CountAsync();


            var activeEmployees =
                await _context.Employees.CountAsync(
                    employee =>
                        employee.EmployeeStatus == "Active"
                );


            var pendingEmployees =
                await _context.Employees.CountAsync(
                    employee =>
                        employee.EmployeeStatus == "Pending"
                );


            var inactiveEmployees =
                await _context.Employees.CountAsync(
                    employee =>
                        employee.EmployeeStatus == "Inactive"
                );


            // ================================================
            // PRODUCTION
            // ================================================

            var totalProductionBatches =
                await _context.ProductionBatches.CountAsync();


            var pendingQualityChecks =
                await _context.ProductionBatches.CountAsync(
                    batch =>
                        batch.QualityStatus == "Pending"
                );


            var approvedBatches =
                await _context.ProductionBatches.CountAsync(
                    batch =>
                        batch.QualityStatus == "Approved"
                );


            var rejectedBatches =
                await _context.ProductionBatches.CountAsync(
                    batch =>
                        batch.QualityStatus == "Rejected"
                );


            // ================================================
            // RAW MATERIAL INVENTORY
            // ================================================

            var totalRawMaterials =
                await _context.RawMaterials.CountAsync();


            var lowStockMaterials =
                await _context.RawMaterials.CountAsync(
                    material =>
                        material.CurrentStock <=
                        material.ReorderLevel
                );


            // ================================================
            // FINISHED GOODS
            // ================================================

            var totalFinishedGoods =
                await _context.FinishedGoods.CountAsync();


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
    }
}