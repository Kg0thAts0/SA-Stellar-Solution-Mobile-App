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

        public TokenService(IConfiguration configuration)
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
        // CREATE EMPLOYEE JWT
        // ====================================================

        public (string Token, DateTime ExpiresAt)
            CreateToken(Employee employee)
        {
            ArgumentNullException.ThrowIfNull(employee);

            // Employees without assigned roles must not
            // receive an authenticated employee JWT.
            if (string.IsNullOrWhiteSpace(employee.Role))
            {
                throw new InvalidOperationException(
                    "Cannot generate an employee token " +
                    "because the employee has no assigned role."
                );
            }

            if (string.IsNullOrWhiteSpace(employee.FullName) ||
                string.IsNullOrWhiteSpace(employee.EmailAddress))
            {
                throw new InvalidOperationException(
                    "Employee name or email address is missing."
                );
            }

            if (string.IsNullOrWhiteSpace(_jwtSettings.Key))
            {
                throw new InvalidOperationException(
                    "JWT signing key is missing."
                );
            }

            if (_jwtSettings.ExpirationMinutes <= 0)
            {
                throw new InvalidOperationException(
                    "JWT expiration must be greater than zero."
                );
            }

            var expiresAt =
                DateTime.UtcNow.AddMinutes(
                    _jwtSettings.ExpirationMinutes
                );

            // =================================================
            // EMPLOYEE CLAIMS
            // =================================================

            var claims = new List<Claim>
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

            var key = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(_jwtSettings.Key)
            );

            var credentials = new SigningCredentials(
                key,
                SecurityAlgorithms.HmacSha256
            );

            // =================================================
            // CREATE JWT
            // =================================================

            var token = new JwtSecurityToken(
                issuer: _jwtSettings.Issuer,
                audience: _jwtSettings.Audience,
                claims: claims,
                expires: expiresAt,
                signingCredentials: credentials
            );

            var tokenString =
                new JwtSecurityTokenHandler()
                    .WriteToken(token);

            return (tokenString, expiresAt);
        }
    }
}
