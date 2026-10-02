namespace QAClothingFactory.API.DTOs.Admin
{
    public class AdminDashboardResponse
    {
        public bool Success { get; set; }

        // Employees
        public int TotalEmployees { get; set; }
        public int ActiveEmployees { get; set; }
        public int PendingEmployees { get; set; }
        public int InactiveEmployees { get; set; }

        // Production
        public int TotalProductionBatches { get; set; }
        public int PendingQualityChecks { get; set; }
        public int ApprovedBatches { get; set; }
        public int RejectedBatches { get; set; }

        // Inventory
        public int TotalRawMaterials { get; set; }
        public int LowStockMaterials { get; set; }

        // Finished Goods
        public int TotalFinishedGoods { get; set; }

        // Operations
        public int TotalInventoryTransactions { get; set; }
        public int TotalShiftTeams { get; set; }
    }
}
