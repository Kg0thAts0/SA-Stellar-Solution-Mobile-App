using System;

namespace QAClothingFactory.API.Models
{
    public class CustomerOrderStatusHistory
    {
        public int OrderStatusHistoryID { get; set; }

        public int QuotationID { get; set; }

        public int? BatchID { get; set; }

        public string Status { get; set; } = string.Empty;

        public string? Description { get; set; }

        public DateTime CreatedAtUtc { get; set; } = DateTime.UtcNow;

        public int? UpdatedByEmployeeID { get; set; }

        public bool IsVisibleToCustomer { get; set; } = true;
    }
}
