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
        // Contains the employee's password HASH.
        //
        // The password must NEVER be returned through
        // API responses.
        // ================================================

        public string Password { get; set; } = string.Empty;


        // ================================================
        // ROLE
        // ================================================
        //
        // Role is nullable because newly registered
        // employees do not receive a factory role
        // automatically.
        //
        // The Admin assigns the appropriate role before
        // activating the employee account.
        // ================================================

        public string? Role { get; set; }


        // ================================================
        // ACCOUNT STATUS
        // ================================================
        //
        // Examples:
        //
        // Pending
        // Active
        // Inactive
        // ================================================

        public string? EmployeeStatus { get; set; }


        // ================================================
        // AUDIT INFORMATION
        // ================================================

        public DateTime? CreatedAt { get; set; }

        public int? CreatedBy { get; set; }
    }
}