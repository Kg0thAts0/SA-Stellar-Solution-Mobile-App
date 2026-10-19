
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/database-test")]
    public class DatabaseTestController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        private readonly IWebHostEnvironment _environment;

        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public DatabaseTestController(
            ApplicationDbContext context,
            IWebHostEnvironment environment
        )
        {
            _context = context;
            _environment = environment;
        }

        // ====================================================
        // GET: api/database-test
        // Available only in Development
        // ====================================================

        [HttpGet]
        public async Task<IActionResult> TestConnection()
        {
            // Disable this endpoint in Production.
            // Prevent public access to database information.

            if (!_environment.IsDevelopment())
            {
                return NotFound();
            }

            try
            {
                // Check whether the database is reachable.

                var canConnect = await _context.Database
                    .CanConnectAsync();

                if (!canConnect)
                {
                    return StatusCode(
                        500,
                        new
                        {
                            success = false,
                            message =
                                "Could not connect to the QA Clothing Factory database."
                        }
                    );
                }

                // Count employees without modifying data.

                var employeeCount = await _context.Employees
                    .AsNoTracking()
                    .CountAsync();

                return Ok(
                    new
                    {
                        success = true,
                        message =
                            "Successfully connected to the QA Clothing Factory database.",
                        database = "qaclothingfactory",
                        employeeCount = employeeCount
                    }
                );
            }
            catch (Exception ex)
            {
                return StatusCode(
                    500,
                    new
                    {
                        success = false,
                        message =
                            "Database connection test failed.",
                        error = ex.Message
                    }
                );
            }
        }
    }
}
