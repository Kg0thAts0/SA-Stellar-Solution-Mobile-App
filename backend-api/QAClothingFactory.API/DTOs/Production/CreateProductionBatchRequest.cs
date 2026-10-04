using System.ComponentModel.DataAnnotations;

namespace QAClothingFactory.API.DTOs.Production
{
    public class CreateProductionBatchRequest
    {
        [Required]
        [MaxLength(50)]
        public string BatchNumber { get; set; } = string.Empty;


        [Required]
        public int ProductID { get; set; }


        [Range(
            0.01,
            double.MaxValue,
            ErrorMessage = "Quantity produced must be greater than zero."
        )]
        public decimal QuantityProduced { get; set; }


        [Range(
            0,
            double.MaxValue,
            ErrorMessage = "Quantity defective cannot be negative."
        )]
        public decimal? QuantityDefective { get; set; }


        public string? RawMaterialUsed { get; set; }


        [Required]
        public DateTime ProductionDate { get; set; }


        public string? Shift { get; set; }


        [MaxLength(20)]
        public string? LineNumber { get; set; }


        public int? SupervisorID { get; set; }


        public TimeSpan? StartTime { get; set; }


        public TimeSpan? EndTime { get; set; }
    }
}