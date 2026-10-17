namespace QAClothingFactory.API.DTOs.Admin
{
    public class FactoryOverviewResponse
    {
        public EmployeeOverview Employees { get; set; } = new();
        public ShiftOverview ShiftTeams { get; set; } = new();
        public ProductionOverview Production { get; set; } = new();
        public InventoryOverview Inventory { get; set; } = new();
        public QualityOverview Quality { get; set; } = new();
    }

    public class EmployeeOverview
    {
        public int TotalEmployees { get; set; }
        public int ActiveEmployees { get; set; }
        public int PendingEmployees { get; set; }
        public int InactiveEmployees { get; set; }
    }

    public class ShiftOverview
    {
        public int TodayShiftTeams { get; set; }
        public int MorningTeams { get; set; }
        public int AfternoonTeams { get; set; }
        public int NightTeams { get; set; }
        public int EmployeesScheduledToday { get; set; }
    }

    public class ProductionOverview
    {
        public int TotalBatches { get; set; }
        public int TodayBatches { get; set; }

        public decimal TotalQuantityProduced { get; set; }
        public decimal TotalQuantityDefective { get; set; }

        public decimal TodayQuantityProduced { get; set; }
        public decimal TodayQuantityDefective { get; set; }
    }

    public class InventoryOverview
    {
        public int TotalRawMaterials { get; set; }
        public int LowStockMaterials { get; set; }

        public int TotalFinishedGoods { get; set; }

        public decimal RawMaterialStockQuantity { get; set; }
        public decimal FinishedGoodsStockQuantity { get; set; }

        public int TotalInventoryTransactions { get; set; }
    }

    public class QualityOverview
    {
        public int PendingBatches { get; set; }
        public int ApprovedBatches { get; set; }
        public int RejectedBatches { get; set; }

        public int PendingFinishedGoods { get; set; }
        public int ApprovedFinishedGoods { get; set; }
        public int RejectedFinishedGoods { get; set; }
    }
}