using System;

namespace QAClothingFactory.API.Models
{
    public class EmailNotificationLog
    {
        public int EmailNotificationLogID { get; set; }

        // Customer quotation associated with this email.
        public int QuotationID { get; set; }

        // Optional production batch.
        public int? BatchID { get; set; }

        // Recipient email address.
        public string RecipientEmail { get; set; } = string.Empty;

        // Email subject and content.
        public string Subject { get; set; } = string.Empty;

        public string Body { get; set; } = string.Empty;

        // Examples: QuotationReady, OrderAccepted,
        // ProductionStarted, QualityApproved, Dispatched.
        public string NotificationType { get; set; } = string.Empty;

        // Pending, Processing, Sent or Failed.
        public string Status { get; set; } = "Pending";

        // Unique identifier to prevent duplicate event emails.
        public string DeduplicationKey { get; set; } = string.Empty;

        // Number of delivery attempts.
        public int RetryCount { get; set; } = 0;

        public int MaxRetries { get; set; } = 5;

        // UTC timestamps.
        public DateTime CreatedAtUtc { get; set; } = DateTime.UtcNow;

        public DateTime? LastAttemptAtUtc { get; set; }

        public DateTime? NextAttemptAtUtc { get; set; }

        public DateTime? SentAtUtc { get; set; }

        // Stores the most recent delivery error.
        public string? LastError { get; set; }
    }
}
