
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using Microsoft.Extensions.Configuration;
using QAClothingFactory.API.Models;
using QAClothingFactory.API.Services;
using Xunit;

namespace QAClothingFactory.Tests
{
    public class TokenServiceTests
    {
        private static TokenService CreateTokenService()
        {
            var settings = new Dictionary<string, string?>
            {
                ["Jwt:Key"] =
                    "TestSigningKeyForQAClothingFactory2026Secure123456",
                ["Jwt:Issuer"] = "QAClothingFactory.Tests",
                ["Jwt:Audience"] = "QAClothingFactory.TestUsers",
                ["Jwt:ExpirationMinutes"] = "60"
            };

            var configuration = new ConfigurationBuilder()
                .AddInMemoryCollection(settings)
                .Build();

            return new TokenService(configuration);
        }

        private static Employee CreateTestEmployee()
        {
            return new Employee
            {
                EmployeeID = 101,
                FullName = "Test Employee",
                EmailAddress = "employee@example.com",
                Role = "Admin",
                EmployeeStatus = "Active"
            };
        }

        [Fact]
        public void CreateToken_ValidEmployee_ReturnsJwtToken()
        {
            // Arrange
            var service = CreateTokenService();
            var employee = CreateTestEmployee();

            // Act
            var result = service.CreateToken(employee);

            // Assert
            Assert.False(string.IsNullOrWhiteSpace(result.Token));

            var handler = new JwtSecurityTokenHandler();

            Assert.True(handler.CanReadToken(result.Token));
        }

        [Fact]
        public void CreateToken_ValidEmployee_ContainsEmployeeClaims()
        {
            // Arrange
            var service = CreateTokenService();
            var employee = CreateTestEmployee();

            // Act
            var result = service.CreateToken(employee);

            var token = new JwtSecurityTokenHandler()
                .ReadJwtToken(result.Token);

            // Assert
            Assert.Contains(
                token.Claims,
                claim =>
                    claim.Type == ClaimTypes.NameIdentifier &&
                    claim.Value == "101"
            );

            Assert.Contains(
                token.Claims,
                claim =>
                    claim.Type == ClaimTypes.Name &&
                    claim.Value == "Test Employee"
            );

            Assert.Contains(
                token.Claims,
                claim =>
                    claim.Type == ClaimTypes.Email &&
                    claim.Value == "employee@example.com"
            );

            Assert.Contains(
                token.Claims,
                claim =>
                    claim.Type == ClaimTypes.Role &&
                    claim.Value == "Admin"
            );
        }

        [Fact]
        public void CreateToken_ValidEmployee_HasCorrectIssuerAndAudience()
        {
            // Arrange
            var service = CreateTokenService();
            var employee = CreateTestEmployee();

            // Act
            var result = service.CreateToken(employee);

            var token = new JwtSecurityTokenHandler()
                .ReadJwtToken(result.Token);

            // Assert
            Assert.Equal(
                "QAClothingFactory.Tests",
                token.Issuer
            );

            Assert.Contains(
                "QAClothingFactory.TestUsers",
                token.Audiences
            );
        }

        [Fact]
        public void CreateToken_ValidEmployee_HasCorrectExpiration()
        {
            // Arrange
            var service = CreateTokenService();
            var employee = CreateTestEmployee();

            var beforeCreation = DateTime.UtcNow;

            // Act
            var result = service.CreateToken(employee);

            var afterCreation = DateTime.UtcNow;

            // Assert
            Assert.InRange(
                result.ExpiresAt,
                beforeCreation.AddMinutes(60),
                afterCreation.AddMinutes(60)
            );

            var token = new JwtSecurityTokenHandler()
                .ReadJwtToken(result.Token);

            Assert.InRange(
                Math.Abs(
                    (token.ValidTo - result.ExpiresAt)
                    .TotalSeconds
                ),
                0,
                1
            );
        }

        [Fact]
        public void Constructor_MissingJwtConfiguration_ThrowsException()
        {
            // Arrange
            var configuration = new ConfigurationBuilder()
                .AddInMemoryCollection(
                    new Dictionary<string, string?>()
                )
                .Build();

            // Act and Assert
            var exception = Assert.Throws<InvalidOperationException>(
                () => new TokenService(configuration)
            );

            Assert.Equal(
                "JWT configuration was not found.",
                exception.Message
            );
        }
    }
}
