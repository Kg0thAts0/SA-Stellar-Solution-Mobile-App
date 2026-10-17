using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.DTOs.Admin;
using QAClothingFactory.API.Models;
using QAClothingFactory.API.Services;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/admin/shift-teams")]
    [Authorize(Roles = "Admin")]
    public class AdminShiftTeamsController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        private readonly SystemActivityService _systemActivityService;


        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public AdminShiftTeamsController(
            ApplicationDbContext context,
            SystemActivityService systemActivityService
        )
        {
            _context = context;
            _systemActivityService = systemActivityService;
        }


        // ====================================================
        // GET ALL SHIFT TEAMS
        // ====================================================
        //
        // GET /api/admin/shift-teams
        //
        // ====================================================

        [HttpGet]
        public async Task<ActionResult<IEnumerable<ShiftTeamResponse>>>
            GetShiftTeams()
        {
            var shiftTeams =
                await (
                    from shiftTeam in _context.ShiftTeams

                    join supervisor in _context.Employees
                        on shiftTeam.SupervisorID
                        equals supervisor.EmployeeID
                        into supervisorGroup

                    from supervisor in
                        supervisorGroup.DefaultIfEmpty()

                    orderby
                        shiftTeam.ShiftDate descending,
                        shiftTeam.ShiftTeamID descending

                    select new ShiftTeamResponse
                    {
                        ShiftTeamID =
                            shiftTeam.ShiftTeamID,

                        ShiftDate =
                            shiftTeam.ShiftDate,

                        Shift =
                            shiftTeam.Shift,

                        SupervisorID =
                            shiftTeam.SupervisorID,

                        SupervisorName =
                            supervisor != null
                                ? supervisor.FullName
                                : "Unknown Supervisor",

                        LineNumber =
                            shiftTeam.LineNumber,

                        TotalEmployees =
                            shiftTeam.TotalEmployees,

                        Notes =
                            shiftTeam.Notes,

                        CreatedAt =
                            shiftTeam.CreatedAt
                    }
                )
                .AsNoTracking()
                .ToListAsync();

            return Ok(shiftTeams);
        }


        // ====================================================
        // GET ACTIVE SUPERVISORS
        // ====================================================
        //
        // GET /api/admin/shift-teams/supervisors
        //
        // ====================================================

        [HttpGet("supervisors")]
        public async Task<IActionResult>
            GetSupervisors()
        {
            var supervisors =
                await _context
                    .Employees
                    .AsNoTracking()
                    .Where(
                        employee =>
                            employee.Role == "Supervisor" &&
                            employee.EmployeeStatus == "Active"
                    )
                    .OrderBy(
                        employee =>
                            employee.FullName
                    )
                    .Select(
                        employee =>
                            new
                            {
                                employeeID =
                                    employee.EmployeeID,

                                fullName =
                                    employee.FullName,

                                emailAddress =
                                    employee.EmailAddress
                            }
                    )
                    .ToListAsync();

            return Ok(supervisors);
        }


        // ====================================================
        // GET SHIFT TEAM BY ID
        // ====================================================
        //
        // GET /api/admin/shift-teams/1
        //
        // ====================================================

        [HttpGet("{shiftTeamId:int}")]
        public async Task<ActionResult<ShiftTeamResponse>>
            GetShiftTeam(
                int shiftTeamId
            )
        {
            if (shiftTeamId <= 0)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Invalid shift team ID."
                    }
                );
            }


            var shiftTeam =
                await (
                    from team in _context.ShiftTeams

                    join supervisor in _context.Employees
                        on team.SupervisorID
                        equals supervisor.EmployeeID
                        into supervisorGroup

                    from supervisor in
                        supervisorGroup.DefaultIfEmpty()

                    where
                        team.ShiftTeamID ==
                        shiftTeamId

                    select new ShiftTeamResponse
                    {
                        ShiftTeamID =
                            team.ShiftTeamID,

                        ShiftDate =
                            team.ShiftDate,

                        Shift =
                            team.Shift,

                        SupervisorID =
                            team.SupervisorID,

                        SupervisorName =
                            supervisor != null
                                ? supervisor.FullName
                                : "Unknown Supervisor",

                        LineNumber =
                            team.LineNumber,

                        TotalEmployees =
                            team.TotalEmployees,

                        Notes =
                            team.Notes,

                        CreatedAt =
                            team.CreatedAt
                    }
                )
                .AsNoTracking()
                .FirstOrDefaultAsync();


            if (shiftTeam == null)
            {
                return NotFound(
                    new
                    {
                        success = false,
                        message =
                            "Shift team was not found."
                    }
                );
            }


            return Ok(shiftTeam);
        }


        // ====================================================
        // CREATE SHIFT TEAM
        // ====================================================
        //
        // POST /api/admin/shift-teams
        //
        // ====================================================

        [HttpPost]
        public async Task<IActionResult>
            CreateShiftTeam(
                [FromBody]
                CreateShiftTeamRequest request
            )
        {
            // ================================================
            // VALIDATE DATE
            // ================================================

            if (request.ShiftDate == default)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "A shift date is required."
                    }
                );
            }


            // ================================================
            // VALIDATE SHIFT
            // ================================================

            var selectedShift =
                NormaliseShift(
                    request.Shift
                );


            if (selectedShift == null)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Shift must be Morning, Afternoon, or Night."
                    }
                );
            }


            // ================================================
            // VALIDATE SUPERVISOR
            // ================================================

            var supervisor =
                await GetValidSupervisor(
                    request.SupervisorID
                );


            if (supervisor == null)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Please select an active employee with the Supervisor role."
                    }
                );
            }


            // ================================================
            // VALIDATE TOTAL EMPLOYEES
            // ================================================

            if (
                request.TotalEmployees.HasValue &&
                request.TotalEmployees.Value < 0
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Total employees cannot be negative."
                    }
                );
            }


            // ================================================
            // CREATE SHIFT TEAM
            // ================================================

            var shiftTeam =
                new ShiftTeam
                {
                    ShiftDate =
                        request.ShiftDate.Date,

                    Shift =
                        selectedShift,

                    SupervisorID =
                        supervisor.EmployeeID,

                    LineNumber =
                        NormaliseOptionalText(
                            request.LineNumber
                        ),

                    TotalEmployees =
                        request.TotalEmployees,

                    Notes =
                        NormaliseOptionalText(
                            request.Notes
                        ),

                    CreatedAt =
                        DateTime.Now
                };


            // ================================================
            // SAVE SHIFT TEAM
            // ================================================

            _context
                .ShiftTeams
                .Add(
                    shiftTeam
                );


            await _context
                .SaveChangesAsync();


            // ================================================
            // SYSTEM ACTIVITY
            // ================================================

            await _systemActivityService
                .LogActivityAsync(
                    actionType:
                        "SHIFT_TEAM_CREATED",

                    description:
                        $"Created Shift Team #{shiftTeam.ShiftTeamID} " +
                        $"for the {shiftTeam.Shift} shift on " +
                        $"{shiftTeam.ShiftDate:yyyy-MM-dd}.",

                    entityType:
                        "ShiftTeam",

                    entityId:
                        shiftTeam.ShiftTeamID
                );


            // ================================================
            // RESPONSE
            // ================================================

            var response =
                CreateShiftTeamResponse(
                    shiftTeam,
                    supervisor.FullName
                );


            return CreatedAtAction(
                nameof(GetShiftTeam),

                new
                {
                    shiftTeamId =
                        shiftTeam.ShiftTeamID
                },

                new
                {
                    success = true,

                    message =
                        $"Shift team #{shiftTeam.ShiftTeamID} has been created successfully.",

                    shiftTeam =
                        response
                }
            );
        }


        // ====================================================
        // UPDATE SHIFT TEAM
        // ====================================================
        //
        // PUT /api/admin/shift-teams/{shiftTeamId}
        //
        // ====================================================

        [HttpPut("{shiftTeamId:int}")]
        public async Task<IActionResult>
            UpdateShiftTeam(
                int shiftTeamId,

                [FromBody]
                UpdateShiftTeamRequest request
            )
        {
            // ================================================
            // VALIDATE ID
            // ================================================

            if (shiftTeamId <= 0)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Invalid shift team ID."
                    }
                );
            }


            // ================================================
            // FIND SHIFT TEAM
            // ================================================

            var shiftTeam =
                await _context
                    .ShiftTeams
                    .FirstOrDefaultAsync(
                        team =>
                            team.ShiftTeamID ==
                            shiftTeamId
                    );


            if (shiftTeam == null)
            {
                return NotFound(
                    new
                    {
                        success = false,
                        message =
                            "Shift team was not found."
                    }
                );
            }


            // ================================================
            // VALIDATE DATE
            // ================================================

            if (request.ShiftDate == default)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "A shift date is required."
                    }
                );
            }


            // ================================================
            // VALIDATE SHIFT
            // ================================================

            var selectedShift =
                NormaliseShift(
                    request.Shift
                );


            if (selectedShift == null)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Shift must be Morning, Afternoon, or Night."
                    }
                );
            }


            // ================================================
            // VALIDATE SUPERVISOR
            // ================================================

            var supervisor =
                await GetValidSupervisor(
                    request.SupervisorID
                );


            if (supervisor == null)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Please select an active employee with the Supervisor role."
                    }
                );
            }


            // ================================================
            // VALIDATE TOTAL EMPLOYEES
            // ================================================

            if (
                request.TotalEmployees.HasValue &&
                request.TotalEmployees.Value < 0
            )
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Total employees cannot be negative."
                    }
                );
            }


            // ================================================
            // UPDATE SHIFT TEAM
            // ================================================

            shiftTeam.ShiftDate =
                request.ShiftDate.Date;

            shiftTeam.Shift =
                selectedShift;

            shiftTeam.SupervisorID =
                supervisor.EmployeeID;

            shiftTeam.LineNumber =
                NormaliseOptionalText(
                    request.LineNumber
                );

            shiftTeam.TotalEmployees =
                request.TotalEmployees;

            shiftTeam.Notes =
                NormaliseOptionalText(
                    request.Notes
                );


            // ================================================
            // SAVE CHANGES
            // ================================================

            await _context
                .SaveChangesAsync();


            // ================================================
            // SYSTEM ACTIVITY
            // ================================================

            await _systemActivityService
                .LogActivityAsync(
                    actionType:
                        "SHIFT_TEAM_UPDATED",

                    description:
                        $"Updated Shift Team #{shiftTeam.ShiftTeamID}. " +
                        $"Shift: {shiftTeam.Shift}, " +
                        $"Date: {shiftTeam.ShiftDate:yyyy-MM-dd}, " +
                        $"Line: {shiftTeam.LineNumber ?? "Not specified"}, " +
                        $"Employees: " +
                        $"{shiftTeam.TotalEmployees?.ToString() ?? "Not specified"}.",

                    entityType:
                        "ShiftTeam",

                    entityId:
                        shiftTeam.ShiftTeamID
                );


            // ================================================
            // RESPONSE
            // ================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        $"Shift team #{shiftTeam.ShiftTeamID} has been updated successfully.",

                    shiftTeam =
                        CreateShiftTeamResponse(
                            shiftTeam,
                            supervisor.FullName
                        )
                }
            );
        }


        // ====================================================
        // DELETE SHIFT TEAM
        // ====================================================
        //
        // DELETE /api/admin/shift-teams/{shiftTeamId}
        //
        // ====================================================

        [HttpDelete("{shiftTeamId:int}")]
        public async Task<IActionResult>
            DeleteShiftTeam(
                int shiftTeamId
            )
        {
            // ================================================
            // VALIDATE ID
            // ================================================

            if (shiftTeamId <= 0)
            {
                return BadRequest(
                    new
                    {
                        success = false,
                        message =
                            "Invalid shift team ID."
                    }
                );
            }


            // ================================================
            // FIND SHIFT TEAM
            // ================================================

            var shiftTeam =
                await _context
                    .ShiftTeams
                    .FirstOrDefaultAsync(
                        team =>
                            team.ShiftTeamID ==
                            shiftTeamId
                    );


            if (shiftTeam == null)
            {
                return NotFound(
                    new
                    {
                        success = false,
                        message =
                            "Shift team was not found."
                    }
                );
            }


            // ================================================
            // STORE INFORMATION BEFORE DELETE
            // ================================================

            var deletedShiftTeamId =
                shiftTeam.ShiftTeamID;

            var deletedShiftDate =
                shiftTeam.ShiftDate;

            var deletedShift =
                shiftTeam.Shift;


            // ================================================
            // DELETE SHIFT TEAM
            // ================================================

            _context
                .ShiftTeams
                .Remove(
                    shiftTeam
                );


            await _context
                .SaveChangesAsync();


            // ================================================
            // SYSTEM ACTIVITY
            // ================================================

            await _systemActivityService
                .LogActivityAsync(
                    actionType:
                        "SHIFT_TEAM_DELETED",

                    description:
                        $"Deleted Shift Team #{deletedShiftTeamId} " +
                        $"for the {deletedShift} shift on " +
                        $"{deletedShiftDate:yyyy-MM-dd}.",

                    entityType:
                        "ShiftTeam",

                    entityId:
                        deletedShiftTeamId
                );


            // ================================================
            // RESPONSE
            // ================================================

            return Ok(
                new
                {
                    success = true,

                    message =
                        $"Shift team #{deletedShiftTeamId} has been deleted successfully.",

                    shiftTeam =
                        new
                        {
                            shiftTeamID =
                                deletedShiftTeamId,

                            shiftDate =
                                deletedShiftDate,

                            shift =
                                deletedShift
                        }
                }
            );
        }


        // ====================================================
        // GET VALID SUPERVISOR
        // ====================================================

        private async Task<Employee?>
            GetValidSupervisor(
                int supervisorId
            )
        {
            if (supervisorId <= 0)
            {
                return null;
            }


            return await _context
                .Employees
                .AsNoTracking()
                .FirstOrDefaultAsync(
                    employee =>
                        employee.EmployeeID ==
                            supervisorId &&

                        employee.Role ==
                            "Supervisor" &&

                        employee.EmployeeStatus ==
                            "Active"
                );
        }


        // ====================================================
        // NORMALISE SHIFT
        // ====================================================

        private static string?
            NormaliseShift(
                string? shift
            )
        {
            if (
                string.IsNullOrWhiteSpace(
                    shift
                )
            )
            {
                return null;
            }


            var validShifts =
                new[]
                {
                    "Morning",
                    "Afternoon",
                    "Night"
                };


            return validShifts
                .FirstOrDefault(
                    validShift =>
                        string.Equals(
                            validShift,
                            shift.Trim(),
                            StringComparison.OrdinalIgnoreCase
                        )
                );
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


        // ====================================================
        // CREATE RESPONSE
        // ====================================================

        private static ShiftTeamResponse
            CreateShiftTeamResponse(
                ShiftTeam shiftTeam,
                string supervisorName
            )
        {
            return new ShiftTeamResponse
            {
                ShiftTeamID =
                    shiftTeam.ShiftTeamID,

                ShiftDate =
                    shiftTeam.ShiftDate,

                Shift =
                    shiftTeam.Shift,

                SupervisorID =
                    shiftTeam.SupervisorID,

                SupervisorName =
                    supervisorName,

                LineNumber =
                    shiftTeam.LineNumber,

                TotalEmployees =
                    shiftTeam.TotalEmployees,

                Notes =
                    shiftTeam.Notes,

                CreatedAt =
                    shiftTeam.CreatedAt
            };
        }
    }
}