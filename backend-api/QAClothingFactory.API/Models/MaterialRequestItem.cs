namespace QAClothingFactory.API.Models
{
    public class MaterialRequestItem
    {
        public int RequestItemID { get; set; }

        // Links this item to its material request.
        public int RequestID { get; set; }

        // Links this item to an existing raw material.
        public int MaterialID { get; set; }

        // Quantity requested by the Production Manager.
        public decimal QuantityRequested { get; set; }

        // Quantity actually issued by the Inventory Clerk.
        public decimal QuantityIssued { get; set; } = 0m;

        // Optional notes about the requested material.
        public string? Notes { get; set; }

        // Navigation properties.
        public MaterialRequest MaterialRequest { get; set; } = null!;

        public RawMaterial RawMaterial { get; set; } = null!;
    }
}
