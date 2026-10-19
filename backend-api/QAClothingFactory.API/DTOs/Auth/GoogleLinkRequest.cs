using System.ComponentModel.DataAnnotations;

namespace QAClothingFactory.API.DTOs.Auth
{
    public class GoogleLinkRequest
    {
        [Required(ErrorMessage = "Google ID token is required.")]
        public string IdToken { get; set; } = string.Empty;

        [Required(ErrorMessage = "Current password is required.")]
        public string CurrentPassword { get; set; } = string.Empty;
    }
}
