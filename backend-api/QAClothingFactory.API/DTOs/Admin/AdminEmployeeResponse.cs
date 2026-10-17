namespace QAClothingFactory.API.DTOs.Admin
{
    public class AdminEmployeeResponse
    {
        public int EmployeeID { get; set; }

        public string FullName { get; set; } = string.Empty;

        public string EmailAddress { get; set; } = string.Empty;

        public string Role { get; set; } = string.Empty;

        public string? EmployeeStatus { get; set; }

        public DateTime? CreatedAt { get; set; }

        public int? CreatedBy { get; set; }
    }
}