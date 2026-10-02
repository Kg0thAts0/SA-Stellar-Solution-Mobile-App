namespace QAClothingFactory.API.Models
{
    public class RawMaterial
    {
        public int MaterialID { get; set; }

        public string MaterialCode { get; set; } = string.Empty;

        public string MaterialName { get; set; } = string.Empty;

        public string? Category { get; set; }

        public string Unit { get; set; } = string.Empty;

        public decimal CurrentStock { get; set; }

        public decimal MinimumStock { get; set; }

        public decimal ReorderLevel { get; set; }

        public decimal UnitCost { get; set; }

        public string? SupplierInfo { get; set; }

        public DateTime? LastUpdated { get; set; }

        public int? UpdatedBy { get; set; }
    }
}