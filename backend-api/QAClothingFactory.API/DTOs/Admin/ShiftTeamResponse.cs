namespace QAClothingFactory.API.DTOs.Admin
{
    public class ShiftTeamResponse
    {
        public int ShiftTeamID { get; set; }

        public DateTime ShiftDate { get; set; }

        public string Shift { get; set; } = string.Empty;

        public int SupervisorID { get; set; }

        public string SupervisorName { get; set; } = string.Empty;

        public string? LineNumber { get; set; }

        public int? TotalEmployees { get; set; }

        public string? Notes { get; set; }

        public DateTime? CreatedAt { get; set; }
    }
}