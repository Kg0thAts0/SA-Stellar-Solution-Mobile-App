using System;
using System.ComponentModel.DataAnnotations;

namespace QAClothingFactory.API.DTOs.Production
{
    public sealed class CreateBatchFromQuotationRequest
    {
        [Required]
        [StringLength(50, MinimumLength = 1)]
        public string BatchNumber { get; set; } = string.Empty;

        // Use an existing ProductID or provide a new product.
        public int? ProductID { get; set; }

        [StringLength(50)]
        public string? ProductCode { get; set; }

        [StringLength(100)]
        public string? ProductName { get; set; }

        [StringLength(20)]
        public string? Unit { get; set; }

        [Range(
            typeof(decimal),
            "0",
            "99999999.99",
            ParseLimitsInInvariantCulture = true)]
        public decimal? UnitPrice { get; set; }

        [Range(
            typeof(decimal),
            "0.01",
            "99999999.99",
            ParseLimitsInInvariantCulture = true)]
        public decimal TargetQuantity { get; set; }

        public DateTime ProductionDate { get; set; }

        [StringLength(40)]
        public string? Shift { get; set; }

        [StringLength(20)]
        public string? LineNumber { get; set; }

        public int? SupervisorID { get; set; }

        public TimeSpan? StartTime { get; set; }

        public TimeSpan? EndTime { get; set; }
    }
}
