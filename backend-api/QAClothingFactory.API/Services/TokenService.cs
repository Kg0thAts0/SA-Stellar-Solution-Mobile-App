using Microsoft.IdentityModel.Tokens;
using QAClothingFactory.API.Models;
using QAClothingFactory.API.Settings;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace QAClothingFactory.API.Services
{
    public class TokenService
    {
        private readonly JwtSettings _jwtSettings;


        public TokenService(
            IConfiguration configuration
        )
        {
            _jwtSettings =
                configuration
                    .GetSection("Jwt")
                    .Get<JwtSettings>()
                ?? throw new InvalidOperationException(
                    "JWT configuration was not found."
                );
        }


        // ====================================================
        // CREATE JWT
        // ====================================================

        public (string Token, DateTime ExpiresAt)
            CreateToken(Employee employee)
        {
            var expiresAt =
                DateTime.UtcNow.AddMinutes(
                    _jwtSettings.ExpirationMinutes
                );


            // =================================================
            // CLAIMS
            // =================================================

            var claims =
                new List<Claim>
                {
                    new Claim(
                        ClaimTypes.NameIdentifier,
                        employee.EmployeeID.ToString()
                    ),

                    new Claim(
                        ClaimTypes.Name,
                        employee.FullName
                    ),

                    new Claim(
                        ClaimTypes.Email,
                        employee.EmailAddress
                    ),

                    new Claim(
                        ClaimTypes.Role,
                        employee.Role
                    )
                };


            // =================================================
            // SIGNING KEY
            // =================================================

            var key =
                new SymmetricSecurityKey(
                    Encoding.UTF8.GetBytes(
                        _jwtSettings.Key
                    )
                );


            var credentials =
                new SigningCredentials(
                    key,
                    SecurityAlgorithms.HmacSha256
                );


            // =================================================
            // TOKEN
            // =================================================

            var token =
                new JwtSecurityToken(
                    issuer:
                        _jwtSettings.Issuer,

                    audience:
                        _jwtSettings.Audience,

                    claims:
                        claims,

                    expires:
                        expiresAt,

                    signingCredentials:
                        credentials
                );


            var tokenString =
                new JwtSecurityTokenHandler()
                    .WriteToken(token);


            return (
                tokenString,
                expiresAt
            );
        }
    }
}