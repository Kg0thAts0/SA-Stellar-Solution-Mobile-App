using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Auth;
using QAClothingFactory.API.Models;
using QAClothingFactory.API.Services;
using System.Security.Cryptography;
using System.Text;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        // ====================================================
        // DATABASE
        // ====================================================

        private readonly ApplicationDbContext _context;


        // ====================================================
        // TOKEN SERVICE
        // ====================================================

        private readonly TokenService _tokenService;


        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public AuthController(
            ApplicationDbContext context,
            TokenService tokenService
        )
        {
            _context = context;
            _tokenService = tokenService;
        }


        // ====================================================
        // POST: api/auth/register
        // ====================================================

        [HttpPost("register")]
        public async Task<IActionResult> Register(
            [FromBody] RegisterRequest request
        )
        {
            // =================================================
            // NORMALISE INPUT
            // =================================================

            var firstName =
                request.FirstName.Trim();

            var lastName =
                request.LastName.Trim();

            var email =
                request.Email
                    .Trim()
                    .ToLowerInvariant();


            // =================================================
            // VALIDATE FIRST AND LAST NAME
            // =================================================

            if (
                string.IsNullOrWhiteSpace(firstName) ||
                string.IsNullOrWhiteSpace(lastName)
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "First name and last name are required."
                    }
                );
            }


            // =================================================
            // VALIDATE EMAIL
            // =================================================

            if (string.IsNullOrWhiteSpace(email))
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Email address is required."
                    }
                );
            }


            // =================================================
            // VALIDATE PASSWORD
            // =================================================

            if (
                string.IsNullOrWhiteSpace(
                    request.Password
                ) ||
                request.Password.Length < 8
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Password must contain at least 8 characters."
                    }
                );
            }


            // =================================================
            // CONFIRM PASSWORD
            // =================================================

            if (
                request.Password !=
                request.ConfirmPassword
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Passwords do not match."
                    }
                );
            }


            // =================================================
            // CHECK WHETHER EMAIL ALREADY EXISTS
            // =================================================

            var emailExists =
                await _context.Employees
                    .AnyAsync(
                        employee =>
                            employee.EmailAddress
                                .ToLower() == email
                    );


            if (emailExists)
            {
                return Conflict(
                    new
                    {
                        success = false,

                        message =
                            "An employee account with this email address already exists."
                    }
                );
            }


            // =================================================
            // CREATE FULL NAME
            // =================================================

            var fullName =
                $"{firstName} {lastName}"
                    .Trim();


            // =================================================
            // HASH PASSWORD
            // =================================================

            var passwordHash =
                BCrypt.Net.BCrypt.HashPassword(
                    request.Password
                );


            // =================================================
            // CREATE EMPLOYEE
            // =================================================

            var employee =
                new Employee
                {
                    FullName =
                        fullName,

                    EmailAddress =
                        email,

                    Password =
                        passwordHash,

                    Role =
                        null,

                    EmployeeStatus =
                        "Pending",

                    CreatedAt =
                        DateTime.Now,

                    CreatedBy =
                        null
                };


            // =================================================
            // SAVE EMPLOYEE
            // =================================================

            _context.Employees.Add(
                employee
            );

            await _context.SaveChangesAsync();


            // =================================================
            // CREATE RESPONSE
            // =================================================

            var response =
                new RegisterResponse
                {
                    Success =
                        true,

                    Message =
                        "Registration successful. Your account is pending administrator approval.",

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
                        ?? "Pending"
                };


            return StatusCode(
                StatusCodes.Status201Created,
                response
            );
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
                    .ToLowerInvariant();


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
            // EMPLOYEE MUST HAVE ROLE
            // =================================================

            if (string.IsNullOrWhiteSpace(
                    employee.Role
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
                            "Your account does not have a system role assigned. Please contact an administrator."
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
            // RESPONSE
            // =================================================

            var response =
                new LoginResponse
                {
                    Success =
                        true,

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


            return Ok(
                response
            );
        }


        // ====================================================
        // POST: api/auth/forgot-password
        // ====================================================

        [HttpPost("forgot-password")]
        public async Task<IActionResult> ForgotPassword(
            [FromBody] ForgotPasswordRequest request
        )
        {
            // =================================================
            // VALIDATE EMAIL
            // =================================================

            if (string.IsNullOrWhiteSpace(
                    request.Email
                ))
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Email address is required."
                    }
                );
            }


            // =================================================
            // NORMALISE EMAIL
            // =================================================

            var email =
                request.Email
                    .Trim()
                    .ToLowerInvariant();


            // =================================================
            // FIND EMPLOYEE
            // =================================================

            var employee =
                await _context.Employees
                    .FirstOrDefaultAsync(
                        e =>
                            e.EmailAddress
                                .ToLower() == email
                    );


            // =================================================
            // DON'T REVEAL WHETHER ACCOUNT EXISTS
            // ====================================================
            //
            // In production this prevents attackers from using
            // the endpoint to discover registered email
            // addresses.
            // =================================================

            if (employee == null)
            {
                return Ok(
                    new
                    {
                        success = true,

                        message =
                            "If an account exists for that email address, password reset instructions have been created."
                    }
                );
            }


            // =================================================
            // INVALIDATE PREVIOUS UNUSED TOKENS
            // ====================================================

            var previousTokens =
                await _context.PasswordResets
                    .Where(
                        reset =>
                            reset.EmployeeID ==
                                employee.EmployeeID &&
                            reset.UsedAt == null
                    )
                    .ToListAsync();


            foreach (
                var previousToken
                in previousTokens
            )
            {
                previousToken.UsedAt =
                    DateTime.Now;
            }


            // =================================================
            // GENERATE SECURE RESET TOKEN
            // ====================================================
            //
            // 32 random bytes = 256 bits.
            // The actual token is only returned to the client
            // temporarily during development.
            // =================================================

            var resetToken =
                Convert.ToHexString(
                    RandomNumberGenerator.GetBytes(32)
                );


            // =================================================
            // HASH RESET TOKEN
            // ====================================================
            //
            // The usable token is NOT stored in the database.
            // =================================================

            var tokenHash =
                HashResetToken(
                    resetToken
                );


            // =================================================
            // CREATE PASSWORD RESET RECORD
            // ====================================================

            var passwordReset =
                new PasswordReset
                {
                    EmployeeID =
                        employee.EmployeeID,

                    TokenHash =
                        tokenHash,

                    ExpiresAt =
                        DateTime.Now.AddMinutes(15),

                    UsedAt =
                        null,

                    CreatedAt =
                        DateTime.Now
                };


            _context.PasswordResets.Add(
                passwordReset
            );


            await _context.SaveChangesAsync();


            // =================================================
            // DEVELOPMENT RESPONSE
            // ====================================================
            //
            // IMPORTANT:
            //
            // resetToken is returned ONLY while developing and
            // testing the application.
            //
            // Production must send the token through a secure
            // verified channel such as email and must NOT return
            // it in this HTTP response.
            // =================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        "Password reset request created successfully.",

                    resetToken =
                        resetToken,

                    expiresInMinutes =
                        15
                }
            );
        }


        // ====================================================
        // POST: api/auth/reset-password
        // ====================================================

        [HttpPost("reset-password")]
        public async Task<IActionResult> ResetPassword(
            [FromBody] ResetPasswordRequest request
        )
        {
            // =================================================
            // VALIDATE EMAIL
            // =================================================

            if (string.IsNullOrWhiteSpace(
                    request.Email
                ))
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Email address is required."
                    }
                );
            }


            // =================================================
            // VALIDATE RESET TOKEN
            // =================================================

            if (string.IsNullOrWhiteSpace(
                    request.Token
                ))
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Password reset token is required."
                    }
                );
            }


            // =================================================
            // VALIDATE NEW PASSWORD
            // =================================================

            if (
                string.IsNullOrWhiteSpace(
                    request.NewPassword
                ) ||
                request.NewPassword.Length < 8
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "New password must contain at least 8 characters."
                    }
                );
            }


            // =================================================
            // CONFIRM NEW PASSWORD
            // =================================================

            if (
                request.NewPassword !=
                request.ConfirmPassword
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "Passwords do not match."
                    }
                );
            }


            // =================================================
            // NORMALISE EMAIL
            // =================================================

            var email =
                request.Email
                    .Trim()
                    .ToLowerInvariant();


            // =================================================
            // FIND EMPLOYEE
            // =================================================

            var employee =
                await _context.Employees
                    .FirstOrDefaultAsync(
                        e =>
                            e.EmailAddress
                                .ToLower() == email
                    );


            if (employee == null)
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "The password reset request is invalid or has expired."
                    }
                );
            }


            // =================================================
            // HASH PROVIDED TOKEN
            // =================================================

            var providedTokenHash =
                HashResetToken(
                    request.Token.Trim()
                );


            // =================================================
            // FIND MOST RECENT UNUSED RESET REQUEST
            // ====================================================

            var passwordReset =
                await _context.PasswordResets
                    .Where(
                        reset =>
                            reset.EmployeeID ==
                                employee.EmployeeID &&
                            reset.UsedAt == null
                    )
                    .OrderByDescending(
                        reset =>
                            reset.CreatedAt
                    )
                    .FirstOrDefaultAsync();


            // =================================================
            // RESET REQUEST DOES NOT EXIST
            // =================================================

            if (passwordReset == null)
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "The password reset request is invalid or has expired."
                    }
                );
            }


            // =================================================
            // CHECK EXPIRATION
            // =================================================

            if (
                passwordReset.ExpiresAt <
                DateTime.Now
            )
            {
                passwordReset.UsedAt =
                    DateTime.Now;

                await _context.SaveChangesAsync();

                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "The password reset request has expired. Please request a new one."
                    }
                );
            }


            // =================================================
            // COMPARE TOKEN HASHES
            // ====================================================

            var expectedHashBytes =
                Convert.FromHexString(
                    passwordReset.TokenHash
                );

            var providedHashBytes =
                Convert.FromHexString(
                    providedTokenHash
                );


            var tokenValid =
                expectedHashBytes.Length ==
                    providedHashBytes.Length &&
                CryptographicOperations.FixedTimeEquals(
                    expectedHashBytes,
                    providedHashBytes
                );


            if (!tokenValid)
            {
                return BadRequest(
                    new
                    {
                        success = false,

                        message =
                            "The password reset request is invalid or has expired."
                    }
                );
            }


            // =================================================
            // HASH NEW PASSWORD
            // =================================================

            var newPasswordHash =
                BCrypt.Net.BCrypt.HashPassword(
                    request.NewPassword
                );


            // =================================================
            // UPDATE EMPLOYEE PASSWORD
            // =================================================

            employee.Password =
                newPasswordHash;


            // =================================================
            // MARK TOKEN AS USED
            // =================================================

            passwordReset.UsedAt =
                DateTime.Now;


            // =================================================
            // INVALIDATE ANY OTHER UNUSED RESET TOKENS
            // =================================================

            var otherTokens =
                await _context.PasswordResets
                    .Where(
                        reset =>
                            reset.EmployeeID ==
                                employee.EmployeeID &&
                            reset.PasswordResetID !=
                                passwordReset.PasswordResetID &&
                            reset.UsedAt == null
                    )
                    .ToListAsync();


            foreach (
                var token
                in otherTokens
            )
            {
                token.UsedAt =
                    DateTime.Now;
            }


            // =================================================
            // SAVE PASSWORD CHANGE
            // =================================================

            await _context.SaveChangesAsync();


            // =================================================
            // RESPONSE
            // =================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        "Your password has been reset successfully. You can now sign in with your new password."
                }
            );
        }


        // ====================================================
        // HASH PASSWORD RESET TOKEN
        // ====================================================

        private static string HashResetToken(
            string token
        )
        {
            var tokenBytes =
                Encoding.UTF8.GetBytes(
                    token
                );


            var hashBytes =
                SHA256.HashData(
                    tokenBytes
                );


            return Convert.ToHexString(
                hashBytes
            );
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
                 * Existing PHP bcrypt hashes commonly use
                 * the $2y$ prefix.
                 *
                 * BCrypt.Net uses the same bcrypt algorithm.
                 * Converting $2y$ to $2a$ allows compatibility
                 * with existing PHP-created password hashes.
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