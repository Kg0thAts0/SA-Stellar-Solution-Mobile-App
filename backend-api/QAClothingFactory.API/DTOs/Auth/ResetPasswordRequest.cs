using System.ComponentModel.DataAnnotations;

namespace QAClothingFactory.API.DTOs.Auth
{
    public class ResetPasswordRequest
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; } = string.Empty;


        [Required]
        public string Token { get; set; } = string.Empty;


        [Required]
        [MinLength(8)]
        public string NewPassword { get; set; } = string.Empty;


        [Required]
        public string ConfirmPassword { get; set; } = string.Empty;
    }
}
