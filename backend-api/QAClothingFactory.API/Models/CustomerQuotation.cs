using System;

namespace QAClothingFactory.API.Models
{
    public class CustomerQuotation
    {
        public int QuotationID { get; set; }

        public string ReferenceNumber { get; set; } = string.Empty;

        // Null for legacy quotations created before PIN-based tracking.
        public string? TrackingPinHash { get; set; }

        public string FullName { get; set; } = string.Empty;

        public string? Company { get; set; }

        public string Email { get; set; } = string.Empty;

        public string Phone { get; set; } = string.Empty;

        public string Category { get; set; } = string.Empty;

        public int Quantity { get; set; }

        public DateTime Deadline { get; set; }

        public string Specifications { get; set; } = string.Empty;

        // Existing internal quotation review status.
        public string Status { get; set; } = "Pending";

        public DateTime CreatedAtUtc { get; set; } = DateTime.UtcNow;

        public int? ReviewedByEmployeeID { get; set; }

        public DateTime? ReviewedAtUtc { get; set; }

        // Internal notes are not intended for customer display.
        public string? ManagerNotes { get; set; }

        public decimal? QuotedAmount { get; set; }

        // ====================================================
        // CUSTOMER QUOTATION DECISION
        // ====================================================

        // Pending, Accepted or Rejected.
        // Separate from the Production Manager review status.
        public string CustomerDecision { get; set; } = "Pending";

        // UTC timestamp when the customer made their decision.
        public DateTime? CustomerDecisionAtUtc { get; set; }
    }
}
