namespace QAClothingFactory.API.Models
{
    public class FinishedGood
    {
        public int ProductID { get; set; }

        public string ProductCode { get; set; } = string.Empty;

        public string ProductName { get; set; } = string.Empty;

        public string? Category { get; set; }

        public string Unit { get; set; } = string.Empty;

        public decimal CurrentStock { get; set; }

        public decimal UnitPrice { get; set; }

        public string? ProductionBatchID { get; set; }

        public int? BatchID { get; set; }

        public string? QualityStatus { get; set; }

        public int? QualityInspectorID { get; set; }

        public DateTime? ProductionDate { get; set; }

        public int? RecordedBy { get; set; }

        public DateTime? RecordedAt { get; set; }

        public string? InspectionNotes { get; set; }
    }
}