namespace QAClothingFactory.API.DTOs.Auth
{
    public class RegisterResponse
    {
        // ================================================
        // RESULT
        // ================================================

        public bool Success { get; set; }


        // ================================================
        // MESSAGE
        // ================================================

        public string Message { get; set; } = string.Empty;


        // ================================================
        // EMPLOYEE ID
        // ================================================

        public int EmployeeID { get; set; }


        // ================================================
        // EMPLOYEE NAME
        // ================================================

        public string FullName { get; set; } = string.Empty;


        // ================================================
        // EMAIL ADDRESS
        // ================================================

        public string EmailAddress { get; set; } = string.Empty;


        // ================================================
        // ROLE
        // ================================================
        //
        // Newly registered employees have no role until
        // an Admin assigns one.
        // ================================================

        public string? Role { get; set; }


        // ================================================
        // ACCOUNT STATUS
        // ================================================

        public string EmployeeStatus { get; set; } = string.Empty;
    }
}