using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Admin;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/admin/factory-overview")]
    [Authorize(Roles = "Admin")]
    public class AdminFactoryOverviewController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public AdminFactoryOverviewController(ApplicationDbContext context)
        {
            _context = context;
        }

        // ========================================================
        // GET FACTORY OVERVIEW
        // GET /api/admin/factory-overview
        // ========================================================

        [HttpGet]
        public async Task<ActionResult<FactoryOverviewResponse>> GetFactoryOverview()
        {
            var today = DateTime.Today;
            var tomorrow = today.AddDays(1);

            // ====================================================
            // EMPLOYEES
            // ====================================================

            var totalEmployees =
                await _context.Employees.CountAsync();

            var activeEmployees =
                await _context.Employees.CountAsync(e =>
                    e.EmployeeStatus == "Active");

            var pendingEmployees =
                await _context.Employees.CountAsync(e =>
                    e.EmployeeStatus == "Pending");

            var inactiveEmployees =
                await _context.Employees.CountAsync(e =>
                    e.EmployeeStatus == "Inactive");


            // ====================================================
            // SHIFT TEAMS
            // ====================================================

            var todayShiftTeams =
                await _context.ShiftTeams
                    .Where(s =>
                        s.ShiftDate >= today &&
                        s.ShiftDate < tomorrow)
                    .ToListAsync();

            var morningTeams =
                todayShiftTeams.Count(s =>
                    s.Shift.Equals(
                        "Morning",
                        StringComparison.OrdinalIgnoreCase));

            var afternoonTeams =
                todayShiftTeams.Count(s =>
                    s.Shift.Equals(
                        "Afternoon",
                        StringComparison.OrdinalIgnoreCase));

            var nightTeams =
                todayShiftTeams.Count(s =>
                    s.Shift.Equals(
                        "Night",
                        StringComparison.OrdinalIgnoreCase));

            var employeesScheduledToday =
                todayShiftTeams.Sum(s =>
                    s.TotalEmployees ?? 0);


            // ====================================================
            // PRODUCTION
            // ====================================================

            var productionBatches =
                await _context.ProductionBatches
                    .AsNoTracking()
                    .ToListAsync();

            var todayProductionBatches =
                productionBatches
                    .Where(p =>
                        p.ProductionDate >= today &&
                        p.ProductionDate < tomorrow)
                    .ToList();

            var totalQuantityProduced =
                productionBatches.Sum(p =>
                    p.QuantityProduced);

            var totalQuantityDefective =
                productionBatches.Sum(p =>
                    p.QuantityDefective ?? 0);

            var todayQuantityProduced =
                todayProductionBatches.Sum(p =>
                    p.QuantityProduced);

            var todayQuantityDefective =
                todayProductionBatches.Sum(p =>
                    p.QuantityDefective ?? 0);


            // ====================================================
            // INVENTORY
            // ====================================================

            var rawMaterials =
                await _context.RawMaterials
                    .AsNoTracking()
                    .ToListAsync();

            var finishedGoods =
                await _context.FinishedGoods
                    .AsNoTracking()
                    .ToListAsync();

            var lowStockMaterials =
                rawMaterials.Count(r =>
                    r.CurrentStock <= r.ReorderLevel);

            var rawMaterialStockQuantity =
                rawMaterials.Sum(r =>
                    r.CurrentStock);

            var finishedGoodsStockQuantity =
                finishedGoods.Sum(f =>
                    f.CurrentStock);

            var totalInventoryTransactions =
                await _context.InventoryTransactions.CountAsync();


            // ====================================================
            // QUALITY
            // ====================================================

            var pendingBatches =
                productionBatches.Count(p =>
                    p.QualityStatus == "Pending");

            var approvedBatches =
                productionBatches.Count(p =>
                    p.QualityStatus == "Approved");

            var rejectedBatches =
                productionBatches.Count(p =>
                    p.QualityStatus == "Rejected");

            var pendingFinishedGoods =
                finishedGoods.Count(f =>
                    f.QualityStatus == "Pending");

            var approvedFinishedGoods =
                finishedGoods.Count(f =>
                    f.QualityStatus == "Approved");

            var rejectedFinishedGoods =
                finishedGoods.Count(f =>
                    f.QualityStatus == "Rejected");


            // ====================================================
            // RESPONSE
            // ====================================================

            var response = new FactoryOverviewResponse
            {
                Employees = new EmployeeOverview
                {
                    TotalEmployees = totalEmployees,
                    ActiveEmployees = activeEmployees,
                    PendingEmployees = pendingEmployees,
                    InactiveEmployees = inactiveEmployees
                },

                ShiftTeams = new ShiftOverview
                {
                    TodayShiftTeams = todayShiftTeams.Count,
                    MorningTeams = morningTeams,
                    AfternoonTeams = afternoonTeams,
                    NightTeams = nightTeams,
                    EmployeesScheduledToday =
                        employeesScheduledToday
                },

                Production = new ProductionOverview
                {
                    TotalBatches =
                        productionBatches.Count,

                    TodayBatches =
                        todayProductionBatches.Count,

                    TotalQuantityProduced =
                        totalQuantityProduced,

                    TotalQuantityDefective =
                        totalQuantityDefective,

                    TodayQuantityProduced =
                        todayQuantityProduced,

                    TodayQuantityDefective =
                        todayQuantityDefective
                },

                Inventory = new InventoryOverview
                {
                    TotalRawMaterials =
                        rawMaterials.Count,

                    LowStockMaterials =
                        lowStockMaterials,

                    TotalFinishedGoods =
                        finishedGoods.Count,

                    RawMaterialStockQuantity =
                        rawMaterialStockQuantity,

                    FinishedGoodsStockQuantity =
                        finishedGoodsStockQuantity,

                    TotalInventoryTransactions =
                        totalInventoryTransactions
                },

                Quality = new QualityOverview
                {
                    PendingBatches =
                        pendingBatches,

                    ApprovedBatches =
                        approvedBatches,

                    RejectedBatches =
                        rejectedBatches,

                    PendingFinishedGoods =
                        pendingFinishedGoods,

                    ApprovedFinishedGoods =
                        approvedFinishedGoods,

                    RejectedFinishedGoods =
                        rejectedFinishedGoods
                }
            };

            return Ok(response);
        }
    }
}