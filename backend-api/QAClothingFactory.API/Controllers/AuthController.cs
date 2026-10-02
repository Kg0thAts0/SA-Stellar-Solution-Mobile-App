using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Auth;
using QAClothingFactory.API.Models;
using QAClothingFactory.API.Services;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        private readonly TokenService _tokenService;


        public AuthController(
            ApplicationDbContext context,
            TokenService tokenService
        )
        {
            _context = context;

            _tokenService = tokenService;
        }


        // ====================================================
        // POST: api/auth/login
        // ====================================================

        [HttpPost("login")]
        public async Task<IActionResult> Login(
            [FromBody] LoginRequest request
        )
        {
            // =================================================
            // NORMALISE EMAIL
            // =================================================

            var email =
                request.Email
                    .Trim()
                    .ToLower();


            // =================================================
            // FIND EMPLOYEE
            // =================================================

            var employee =
                await _context.Employees
                    .AsNoTracking()
                    .FirstOrDefaultAsync(
                        e =>
                            e.EmailAddress
                                .ToLower() == email
                    );


            // =================================================
            // EMPLOYEE DOES NOT EXIST
            // =================================================

            if (employee == null)
            {
                await RecordLoginAttempt(
                    request.Email,
                    false
                );

                return Unauthorized(
                    new
                    {
                        success = false,

                        message =
                            "Invalid email address or password."
                    }
                );
            }


            // =================================================
            // CHECK ACCOUNT STATUS
            // =================================================

            if (!string.Equals(
                    employee.EmployeeStatus,
                    "Active",
                    StringComparison.OrdinalIgnoreCase
                ))
            {
                await RecordLoginAttempt(
                    employee.EmailAddress,
                    false
                );

                return StatusCode(
                    StatusCodes.Status403Forbidden,
                    new
                    {
                        success = false,

                        message =
                            "Your account is not active. Please contact an administrator."
                    }
                );
            }


            // =================================================
            // VERIFY PASSWORD
            // =================================================

            var passwordValid =
                VerifyPassword(
                    request.Password,
                    employee.Password
                );


            if (!passwordValid)
            {
                await RecordLoginAttempt(
                    employee.EmailAddress,
                    false
                );

                return Unauthorized(
                    new
                    {
                        success = false,

                        message =
                            "Invalid email address or password."
                    }
                );
            }


            // =================================================
            // CREATE JWT
            // =================================================

            var tokenResult =
                _tokenService.CreateToken(
                    employee
                );


            // =================================================
            // RECORD SUCCESSFUL LOGIN
            // =================================================

            await RecordLoginAttempt(
                employee.EmailAddress,
                true
            );


            // =================================================
            // RETURN SAFE EMPLOYEE INFORMATION
            // =================================================

            var response =
                new LoginResponse
                {
                    Success = true,

                    Message =
                        "Login successful.",

                    Token =
                        tokenResult.Token,

                    ExpiresAt =
                        tokenResult.ExpiresAt,

                    Employee =
                        new EmployeeResponse
                        {
                            EmployeeID =
                                employee.EmployeeID,

                            FullName =
                                employee.FullName,

                            EmailAddress =
                                employee.EmailAddress,

                            Role =
                                employee.Role,

                            EmployeeStatus =
                                employee.EmployeeStatus
                                ?? string.Empty
                        }
                };


            return Ok(response);
        }


        // ====================================================
        // VERIFY EXISTING BCRYPT PASSWORD
        // ====================================================

        private static bool VerifyPassword(
            string password,
            string passwordHash
        )
        {
            if (string.IsNullOrWhiteSpace(
                    passwordHash
                ))
            {
                return false;
            }


            try
            {
                /*
                 * PHP bcrypt commonly stores hashes using
                 * the $2y$ prefix.
                 *
                 * BCrypt.Net works with the bcrypt algorithm,
                 * but normalising $2y$ to $2a$ gives us
                 * compatibility with existing PHP-generated
                 * bcrypt hashes.
                 */

                var compatibleHash =
                    passwordHash.StartsWith(
                        "$2y$",
                        StringComparison.Ordinal
                    )
                        ? "$2a$" +
                          passwordHash.Substring(4)

                        : passwordHash;


                return BCrypt.Net.BCrypt.Verify(
                    password,
                    compatibleHash
                );
            }
            catch
            {
                return false;
            }
        }


        // ====================================================
        // RECORD LOGIN ATTEMPT
        // ====================================================

        private async Task RecordLoginAttempt(
            string email,
            bool success
        )
        {
            var ipAddress =
                HttpContext
                    .Connection
                    .RemoteIpAddress?
                    .ToString()
                ?? "Unknown";


            var loginAttempt =
                new LoginAttempt
                {
                    EmailAddress =
                        email.Trim(),

                    IPAddress =
                        ipAddress,

                    AttemptTime =
                        DateTime.Now,

                    Success =
                        success
                };


            _context.LoginAttempts.Add(
                loginAttempt
            );


            await _context.SaveChangesAsync();
        }
    }
}