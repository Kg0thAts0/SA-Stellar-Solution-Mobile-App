using System.ComponentModel.DataAnnotations;

namespace QAClothingFactory.API.DTOs.Auth
{
    public class GoogleLoginRequest
    {
        [Required(ErrorMessage = "Google ID token is required.")]
        public string IdToken { get; set; } = string.Empty;
    }
}
