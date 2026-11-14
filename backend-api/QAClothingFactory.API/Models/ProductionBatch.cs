using System;

namespace QAClothingFactory.API.Models
{
    public class ProductionBatch
    {
        public int BatchID { get; set; }

        public string BatchNumber { get; set; } = string.Empty;

        // Optional link to a customer quotation.
        // Null means this is a standalone production batch.
        public int? QuotationID { get; set; }

        public int ProductID { get; set; }

        public decimal TargetQuantity { get; set; }

        public decimal QuantityProduced { get; set; }

        public decimal? QuantityDefective { get; set; }

        public string? RawMaterialUsed { get; set; }

        public DateTime ProductionDate { get; set; }

        public string? Shift { get; set; }

        public string? LineNumber { get; set; }

        public int? SupervisorID { get; set; }

        public int? EmployeeID { get; set; }

        public string ProductionStatus { get; set; } = "Planned";

        public string? QualityStatus { get; set; }

        public int? QualityCheckedBy { get; set; }

        public DateTime? QualityCheckDate { get; set; }

        public string? QualityNotes { get; set; }

        public DateTime? RecordedAt { get; set; }

        public TimeSpan? StartTime { get; set; }

        public TimeSpan? EndTime { get; set; }
    }
}
