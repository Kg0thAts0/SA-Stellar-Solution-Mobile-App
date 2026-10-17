using System.Security.Claims;
using Microsoft.AspNetCore.Http;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Models;

namespace QAClothingFactory.API.Services
{
    public class SystemActivityService
    {
        private readonly ApplicationDbContext _context;
        private readonly IHttpContextAccessor _httpContextAccessor;


        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public SystemActivityService(
            ApplicationDbContext context,
            IHttpContextAccessor httpContextAccessor
        )
        {
            _context = context;
            _httpContextAccessor = httpContextAccessor;
        }


        // ====================================================
        // LOG ACTIVITY
        // ====================================================

        public async Task LogActivityAsync(
            string actionType,
            string description,
            string? entityType = null,
            int? entityId = null
        )
        {
            var httpContext =
                _httpContextAccessor.HttpContext;


            // =================================================
            // GET CURRENT EMPLOYEE ID FROM JWT
            // =================================================

            int? employeeId =
                GetCurrentEmployeeId(
                    httpContext
                );


            // =================================================
            // GET IP ADDRESS
            // =================================================

            var ipAddress =
                GetIPAddress(
                    httpContext
                );


            // =================================================
            // CREATE ACTIVITY
            // =================================================

            var activity =
                new SystemActivity
                {
                    EmployeeID =
                        employeeId,

                    ActionType =
                        actionType.Trim(),

                    Description =
                        description.Trim(),

                    EntityType =
                        NormaliseOptionalText(
                            entityType
                        ),

                    EntityID =
                        entityId,

                    ActivityTime =
                        DateTime.Now,

                    IPAddress =
                        ipAddress
                };


            // =================================================
            // SAVE ACTIVITY
            // =================================================

            _context
                .SystemActivities
                .Add(
                    activity
                );

            await _context
                .SaveChangesAsync();
        }


        // ====================================================
        // GET CURRENT EMPLOYEE ID
        // ====================================================

        private static int?
            GetCurrentEmployeeId(
                HttpContext? httpContext
            )
        {
            if (httpContext == null)
            {
                return null;
            }


            var employeeIdValue =
                httpContext
                    .User
                    .FindFirstValue(
                        ClaimTypes.NameIdentifier
                    );


            if (
                int.TryParse(
                    employeeIdValue,
                    out var employeeId
                )
            )
            {
                return employeeId;
            }


            return null;
        }


        // ====================================================
        // GET IP ADDRESS
        // ====================================================

        private static string?
            GetIPAddress(
                HttpContext? httpContext
            )
        {
            if (httpContext == null)
            {
                return null;
            }


            var ipAddress =
                httpContext
                    .Connection
                    .RemoteIpAddress?
                    .ToString();


            if (
                string.IsNullOrWhiteSpace(
                    ipAddress
                )
            )
            {
                return null;
            }


            // Database column is VARCHAR(45).
            if (ipAddress.Length > 45)
            {
                return ipAddress[..45];
            }


            return ipAddress;
        }


        // ====================================================
        // NORMALISE OPTIONAL TEXT
        // ====================================================

        private static string?
            NormaliseOptionalText(
                string? value
            )
        {
            if (
                string.IsNullOrWhiteSpace(
                    value
                )
            )
            {
                return null;
            }


            return value.Trim();
        }
    }
}