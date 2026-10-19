using System.ComponentModel.DataAnnotations;

namespace QAClothingFactory.API.DTOs.Production
{
    public class UpdateProductionBatchRequest
    {
        [Required]
        [MaxLength(50)]
        public string BatchNumber { get; set; } =
            string.Empty;


        public int ProductID { get; set; }


        [Range(
            0.01,
            double.MaxValue,
            ErrorMessage = "Target quantity must be greater than zero."
        )]
        public decimal TargetQuantity { get; set; }


        [Range(
            0,
            double.MaxValue,
            ErrorMessage = "Quantity produced cannot be negative."
        )]
        public decimal QuantityProduced { get; set; }


        [Range(
            0,
            double.MaxValue,
            ErrorMessage = "Quantity defective cannot be negative."
        )]
        public decimal? QuantityDefective { get; set; }


        public string? RawMaterialUsed { get; set; }


        public DateTime ProductionDate { get; set; }


        public string? Shift { get; set; }


        [MaxLength(20)]
        public string? LineNumber { get; set; }


        public int? SupervisorID { get; set; }


        [MaxLength(20)]
        public string ProductionStatus { get; set; } =
            "Planned";


        public TimeSpan? StartTime { get; set; }


        public TimeSpan? EndTime { get; set; }
    }
}