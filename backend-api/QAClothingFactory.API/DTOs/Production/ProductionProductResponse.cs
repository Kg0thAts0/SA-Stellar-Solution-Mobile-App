namespace QAClothingFactory.API.DTOs.Production
{
    public class ProductionProductResponse
    {
        public int ProductID { get; set; }

        public string ProductCode { get; set; } = string.Empty;

        public string ProductName { get; set; } = string.Empty;

        public string? Category { get; set; }

        public string Unit { get; set; } = string.Empty;

        public decimal? CurrentStock { get; set; }
    }
}
