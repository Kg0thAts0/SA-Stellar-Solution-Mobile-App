using System;

namespace QAClothingFactory.API.Models
{
    public class CustomerVerificationToken
    {
        public int CustomerVerificationTokenID { get; set; }

        // The quotation this token belongs to.
        public int QuotationID { get; set; }

        // SHA-256 hash of the random verification token.
        // Never store the original token in the database.
        public string TokenHash { get; set; } = string.Empty;

        // Purpose of the token, such as:
        // QuotationDecision or OrderTracking.
        public string Purpose { get; set; } = string.Empty;

        // UTC creation and expiry timestamps.
        public DateTime CreatedAtUtc { get; set; } =
            DateTime.UtcNow;

        public DateTime ExpiresAtUtc { get; set; }

        // Set when the token is successfully used.
        public DateTime? UsedAtUtc { get; set; }

        // Allows a token to be invalidated before expiry.
        public DateTime? RevokedAtUtc { get; set; }
    }
}
