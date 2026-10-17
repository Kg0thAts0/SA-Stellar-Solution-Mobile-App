namespace QAClothingFactory.API.Models
{
    public class PasswordReset
    {
        // ================================================
        // PRIMARY KEY
        // ================================================

        public int PasswordResetID { get; set; }


        // ================================================
        // EMPLOYEE
        // ================================================

        public int EmployeeID { get; set; }


        // ================================================
        // RESET TOKEN
        // ================================================
        //
        // We never store the original reset token.
        // Only its hash is stored in the database.
        // ================================================

        public string TokenHash { get; set; } = string.Empty;


        // ================================================
        // EXPIRATION
        // ================================================

        public DateTime ExpiresAt { get; set; }


        // ================================================
        // USED DATE
        // ================================================
        //
        // NULL = token has not been used.
        // A date/time = token has already been consumed.
        // ================================================

        public DateTime? UsedAt { get; set; }


        // ================================================
        // CREATED DATE
        // ================================================

        public DateTime CreatedAt { get; set; }


        // ================================================
        // NAVIGATION PROPERTY
        // ================================================

        public Employee? Employee { get; set; }
    }
}