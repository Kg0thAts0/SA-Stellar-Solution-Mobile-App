namespace QAClothingFactory.API.DTOs.Auth
{
    public class LoginResponse
    {
        public bool Success { get; set; }

        public string Message { get; set; } = string.Empty;

        public string Token { get; set; } = string.Empty;

        public DateTime ExpiresAt { get; set; }

        public EmployeeResponse Employee { get; set; } = new();
    }


    public class EmployeeResponse
    {
        public int EmployeeID { get; set; }

        public string FullName { get; set; } = string.Empty;

        public string EmailAddress { get; set; } = string.Empty;

        public string Role { get; set; } = string.Empty;

        public string EmployeeStatus { get; set; } = string.Empty;
    }
}