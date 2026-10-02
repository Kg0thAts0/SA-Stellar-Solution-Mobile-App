namespace QAClothingFactory.API.Models
{
    public class ProductionBatch
    {
        public int BatchID { get; set; }

        public string BatchNumber { get; set; } = string.Empty;

        public int ProductID { get; set; }

        public decimal QuantityProduced { get; set; }

        public decimal? QuantityDefective { get; set; }

        public string? RawMaterialUsed { get; set; }

        public DateTime ProductionDate { get; set; }

        public string? Shift { get; set; }

        public string? LineNumber { get; set; }

        public int? SupervisorID { get; set; }

        public int? EmployeeID { get; set; }

        public string? QualityStatus { get; set; }

        public int? QualityCheckedBy { get; set; }

        public DateTime? QualityCheckDate { get; set; }

        public string? QualityNotes { get; set; }

        public DateTime? RecordedAt { get; set; }

        public TimeSpan? StartTime { get; set; }

        public TimeSpan? EndTime { get; set; }
    }
}