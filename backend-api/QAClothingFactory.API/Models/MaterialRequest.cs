using System;
using System.Collections.Generic;

namespace QAClothingFactory.API.Models
{
    public class MaterialRequest
    {
        public int RequestID { get; set; }

        public int BatchID { get; set; }

        public int RequestedBy { get; set; }

        public DateTime RequestedAt { get; set; } = DateTime.UtcNow;

        // Pending, Partially Issued, Fulfilled or Cancelled.
        public string Status { get; set; } = "Pending";

        public string? Notes { get; set; }

        public ProductionBatch ProductionBatch { get; set; } = null!;

        public Employee RequestingEmployee { get; set; } = null!;

        public ICollection<MaterialRequestItem> Items { get; set; }
            = new List<MaterialRequestItem>();
    }
}
