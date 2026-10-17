using System.ComponentModel.DataAnnotations;

namespace QAClothingFactory.API.DTOs.Auth
{
    public class RegisterRequest
    {
        // ====================================================
        // FIRST NAME
        // ====================================================

        [Required]
        [MaxLength(100)]
        public string FirstName { get; set; } = string.Empty;


        // ====================================================
        // LAST NAME
        // ====================================================

        [Required]
        [MaxLength(100)]
        public string LastName { get; set; } = string.Empty;


        // ====================================================
        // EMAIL
        // ====================================================

        [Required]
        [EmailAddress]
        [MaxLength(255)]
        public string Email { get; set; } = string.Empty;


        // ====================================================
        // PASSWORD
        // ====================================================

        [Required]
        [MinLength(8)]
        [MaxLength(100)]
        public string Password { get; set; } = string.Empty;


        // ====================================================
        // CONFIRM PASSWORD
        // ====================================================

        [Required]
        public string ConfirmPassword { get; set; } = string.Empty;
    }
}