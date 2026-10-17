using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Admin;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/admin/system-activity")]
    [Authorize(Roles = "Admin")]
    public class AdminSystemActivityController : ControllerBase
    {
        private readonly ApplicationDbContext _context;


        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public AdminSystemActivityController(
            ApplicationDbContext context
        )
        {
            _context = context;
        }


        // ====================================================
        // GET SYSTEM ACTIVITY
        // ====================================================
        //
        // GET /api/admin/system-activity
        //
        // Returns newest activities first.
        //
        // ====================================================

        [HttpGet]
        public async Task<ActionResult<IEnumerable<SystemActivityResponse>>>
            GetSystemActivity()
        {
            var activities =
                await (
                    from activity in _context.SystemActivities

                    join employee in _context.Employees
                        on activity.EmployeeID
                        equals employee.EmployeeID
                        into employeeGroup

                    from employee in
                        employeeGroup.DefaultIfEmpty()

                    orderby
                        activity.ActivityTime descending,
                        activity.ActivityID descending

                    select new SystemActivityResponse
                    {
                        ActivityID =
                            activity.ActivityID,

                        EmployeeID =
                            activity.EmployeeID,

                        EmployeeName =
                            employee != null
                                ? employee.FullName
                                : "Unknown User",

                        ActionType =
                            activity.ActionType,

                        Description =
                            activity.Description,

                        EntityType =
                            activity.EntityType,

                        EntityID =
                            activity.EntityID,

                        ActivityTime =
                            activity.ActivityTime,

                        IPAddress =
                            activity.IPAddress
                    }
                )
                .AsNoTracking()
                .ToListAsync();


            return Ok(activities);
        }


        // ====================================================
        // GET SYSTEM ACTIVITY BY ID
        // ====================================================
        //
        // GET /api/admin/system-activity/1
        //
        // ====================================================

        [HttpGet("{activityId:int}")]
        public async Task<ActionResult<SystemActivityResponse>>
            GetSystemActivityById(
                int activityId
            )
        {
            if (activityId <= 0)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Invalid system activity ID."
                    }
                );
            }


            var activity =
                await (
                    from systemActivity in
                        _context.SystemActivities

                    join employee in _context.Employees
                        on systemActivity.EmployeeID
                        equals employee.EmployeeID
                        into employeeGroup

                    from employee in
                        employeeGroup.DefaultIfEmpty()

                    where
                        systemActivity.ActivityID ==
                        activityId

                    select new SystemActivityResponse
                    {
                        ActivityID =
                            systemActivity.ActivityID,

                        EmployeeID =
                            systemActivity.EmployeeID,

                        EmployeeName =
                            employee != null
                                ? employee.FullName
                                : "Unknown User",

                        ActionType =
                            systemActivity.ActionType,

                        Description =
                            systemActivity.Description,

                        EntityType =
                            systemActivity.EntityType,

                        EntityID =
                            systemActivity.EntityID,

                        ActivityTime =
                            systemActivity.ActivityTime,

                        IPAddress =
                            systemActivity.IPAddress
                    }
                )
                .AsNoTracking()
                .FirstOrDefaultAsync();


            if (activity == null)
            {
                return NotFound(
                    new
                    {
                        success = false,
                        message =
                            "System activity was not found."
                    }
                );
            }


            return Ok(activity);
        }
    }
}