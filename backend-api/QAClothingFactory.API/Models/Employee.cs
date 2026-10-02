namespace QAClothingFactory.API.Models
{
    public class Employee
    {
        // ================================================
        // PRIMARY KEY
        // ================================================

        public int EmployeeID { get; set; }


        // ================================================
        // EMPLOYEE INFORMATION
        // ================================================

        public string FullName { get; set; } = string.Empty;

        public string EmailAddress { get; set; } = string.Empty;


        // ================================================
        // AUTHENTICATION
        // ================================================
        //
        // This contains the existing password HASH.
        //
        // We will NEVER return this property from
        // our API responses.
        // ================================================

        public string Password { get; set; } = string.Empty;


        // ================================================
        // ROLE
        // ================================================

        public string Role { get; set; } = string.Empty;


        // ================================================
        // ACCOUNT STATUS
        // ================================================

        public string? EmployeeStatus { get; set; }


        // ================================================
        // AUDIT INFORMATION
        // ================================================

        public DateTime? CreatedAt { get; set; }

        public int? CreatedBy { get; set; }
    }
}