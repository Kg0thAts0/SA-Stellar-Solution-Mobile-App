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

        public DatabaseTestController(
            ApplicationDbContext context
        )
        {
            _context = context;
        }


        // ====================================================
        // GET: api/database-test
        // ====================================================

        [HttpGet]
        public async Task<IActionResult> TestConnection()
        {
            try
            {
                // Ask MariaDB whether the database
                // can actually be reached.

                var canConnect =
                    await _context.Database
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


                // Count existing employees.
                //
                // This is READ ONLY.
                //
                // No database information is modified.

                var employeeCount =
                    await _context.Employees
                        .AsNoTracking()
                        .CountAsync();


                return Ok(
                    new
                    {
                        success = true,

                        message =
                            "Successfully connected to the QA Clothing Factory database.",

                        database =
                            "qaclothingfactory",

                        employeeCount =
                            employeeCount
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

                        error =
                            ex.Message
                    }
                );
            }
        }
    }
}