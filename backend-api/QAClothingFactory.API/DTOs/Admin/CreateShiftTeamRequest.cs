namespace QAClothingFactory.API.DTOs.Admin
{
    public class CreateShiftTeamRequest
    {
        public DateTime ShiftDate { get; set; }

        public string Shift { get; set; } = string.Empty;

        public int SupervisorID { get; set; }

        public string? LineNumber { get; set; }

        public int? TotalEmployees { get; set; }

        public string? Notes { get; set; }
    }
}