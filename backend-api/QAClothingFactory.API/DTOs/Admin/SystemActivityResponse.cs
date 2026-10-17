namespace QAClothingFactory.API.DTOs.Admin
{
    public class SystemActivityResponse
    {
        public int ActivityID { get; set; }

        public int? EmployeeID { get; set; }

        public string EmployeeName { get; set; } =
            "Unknown User";

        public string ActionType { get; set; } =
            string.Empty;

        public string Description { get; set; } =
            string.Empty;

        public string? EntityType { get; set; }

        public int? EntityID { get; set; }

        public DateTime ActivityTime { get; set; }

        public string? IPAddress { get; set; }
    }
}
