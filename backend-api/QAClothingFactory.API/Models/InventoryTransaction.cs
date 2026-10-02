namespace QAClothingFactory.API.Models
{
    public class InventoryTransaction
    {
        public int TransactionID { get; set; }

        public int? MaterialID { get; set; }

        public int? ProductID { get; set; }

        public string TransactionType { get; set; } = string.Empty;

        public decimal Quantity { get; set; }

        public decimal? UnitCost { get; set; }

        public string? ReferenceNumber { get; set; }

        public string? Notes { get; set; }

        public DateTime? TransactionDate { get; set; }

        public int? PerformedBy { get; set; }
    }
}