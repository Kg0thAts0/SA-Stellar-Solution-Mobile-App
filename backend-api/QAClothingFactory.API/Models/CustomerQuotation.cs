using System;

namespace QAClothingFactory.API.Models
{
    public class CustomerQuotation
    {
        public int QuotationID { get; set; }

        public string ReferenceNumber { get; set; } = string.Empty;

        public string FullName { get; set; } = string.Empty;

        public string? Company { get; set; }

        public string Email { get; set; } = string.Empty;

        public string Phone { get; set; } = string.Empty;

        public string Category { get; set; } = string.Empty;

        public int Quantity { get; set; }

        public DateTime Deadline { get; set; }

        public string Specifications { get; set; } = string.Empty;

        public string Status { get; set; } = "Pending";

        public DateTime CreatedAtUtc { get; set; } = DateTime.UtcNow;

        public int? ReviewedByEmployeeID { get; set; }

        public DateTime? ReviewedAtUtc { get; set; }

        public string? ManagerNotes { get; set; }

        public decimal? QuotedAmount { get; set; }
    }
}
