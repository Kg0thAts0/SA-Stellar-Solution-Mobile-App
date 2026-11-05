using System.ComponentModel.DataAnnotations;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Models;

namespace QAClothingFactory.API.Controllers
{
    // ============================================================
    // QA CLOTHING FACTORY - CUSTOMER QUOTATIONS API
    // ============================================================

    [ApiController]
    [Route("api/CustomerQuotations")]
    public class CustomerQuotationsController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        private static readonly string[] AllowedStatuses =
        {
            "Pending",
            "Under Review",
            "Quoted",
            "Approved",
            "Rejected",
            "Cancelled"
        };

        public CustomerQuotationsController(
            ApplicationDbContext context)
        {
            _context = context;
        }

        // ========================================================
        // 1. PUBLIC - SUBMIT CUSTOMER QUOTATION
        // POST /api/CustomerQuotations
        // ========================================================

        [HttpPost]
        [AllowAnonymous]
        public async Task<IActionResult> CreateQuotation(
            [FromBody] CreateCustomerQuotationRequest request)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(new
                {
                    success = false,
                    message = "Invalid quotation details.",
                    errors = ModelState
                });
            }

            if (request.Deadline.Date < DateTime.UtcNow.Date)
            {
                return BadRequest(new
                {
                    success = false,
                    message = "The deadline cannot be in the past."
                });
            }

            var quotation = new CustomerQuotation
            {
                ReferenceNumber =
                    "QA-" +
                    DateTime.UtcNow.ToString("yyyyMMdd") +
                    "-" +
                    Guid.NewGuid().ToString("N")[..12].ToUpperInvariant(),

                FullName = request.FullName.Trim(),

                Company = string.IsNullOrWhiteSpace(request.Company)
                    ? null
                    : request.Company.Trim(),

                Email = request.Email.Trim(),

                Phone = request.Phone.Trim(),

                Category = request.Category.Trim(),

                Quantity = request.Quantity,

                Deadline = request.Deadline,

                Specifications = request.Specifications.Trim(),

                Status = "Pending",

                CreatedAtUtc = DateTime.UtcNow,

                ReviewedByEmployeeID = null,
                ReviewedAtUtc = null,
                ManagerNotes = null,
                QuotedAmount = null
            };

            _context.CustomerQuotations.Add(quotation);

            await _context.SaveChangesAsync();

            return StatusCode(201, new
            {
                success = true,
                message = "Quotation request submitted successfully.",
                quotationID = quotation.QuotationID,
                referenceNumber = quotation.ReferenceNumber,
                status = quotation.Status
            });
        }

        // ========================================================
        // 2. MANAGER - GET ALL CUSTOMER QUOTATIONS
        // GET /api/CustomerQuotations
        // ========================================================

        [HttpGet]
        [Authorize(Roles = "ProductionManager,Admin")]
        public async Task<IActionResult> GetQuotations(
            [FromQuery] string? status,
            [FromQuery] int page = 1,
            [FromQuery] int pageSize = 20)
        {
            if (page < 1 || pageSize < 1 || pageSize > 100)
            {
                return BadRequest(new
                {
                    success = false,
                    message = "Page must be positive and pageSize must be between 1 and 100."
                });
            }

            var query = _context.CustomerQuotations
                .AsNoTracking()
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(status))
            {
                var normalizedStatus = AllowedStatuses
                    .FirstOrDefault(s =>
                        s.Equals(
                            status.Trim(),
                            StringComparison.OrdinalIgnoreCase));

                if (normalizedStatus == null)
                {
                    return BadRequest(new
                    {
                        success = false,
                        message = "Invalid quotation status."
                    });
                }

                query = query.Where(q => q.Status == normalizedStatus);
            }

            var totalCount = await query.CountAsync();

            var quotations = await query
                .OrderByDescending(q => q.CreatedAtUtc)
                .ThenByDescending(q => q.QuotationID)
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .Select(q => new
                {
                    q.QuotationID,
                    q.ReferenceNumber,
                    q.FullName,
                    q.Company,
                    q.Email,
                    q.Phone,
                    q.Category,
                    q.Quantity,
                    q.Deadline,
                    q.Specifications,
                    q.Status,
                    q.CreatedAtUtc,
                    q.ReviewedByEmployeeID,
                    q.ReviewedAtUtc,
                    q.ManagerNotes,
                    q.QuotedAmount
                })
                .ToListAsync();

            return Ok(new
            {
                success = true,
                count = quotations.Count,
                totalCount,
                page,
                pageSize,
                quotations
            });
        }

        // ========================================================
        // 3. MANAGER - GET CUSTOMER QUOTATION BY ID
        // GET /api/CustomerQuotations/{id}
        // ========================================================

        [HttpGet("{id:int}")]
        [Authorize(Roles = "ProductionManager,Admin")]
        public async Task<IActionResult> GetQuotation(int id)
        {
            var quotation = await _context.CustomerQuotations
                .AsNoTracking()
                .FirstOrDefaultAsync(q => q.QuotationID == id);

            if (quotation == null)
            {
                return NotFound(new
                {
                    success = false,
                    message = "Customer quotation was not found."
                });
            }

            return Ok(new
            {
                success = true,
                quotation
            });
        }

        // ========================================================
        // 4. MANAGER - REVIEW CUSTOMER QUOTATION
        // PUT /api/CustomerQuotations/{id}/review
        // ========================================================

        [HttpPut("{id:int}/review")]
        [Authorize(Roles = "ProductionManager,Admin")]
        public async Task<IActionResult> ReviewQuotation(
            int id,
            [FromBody] ReviewCustomerQuotationRequest request)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(new
                {
                    success = false,
                    message = "Invalid review details.",
                    errors = ModelState
                });
            }

            var normalizedStatus = AllowedStatuses
                .FirstOrDefault(s =>
                    s.Equals(
                        request.Status.Trim(),
                        StringComparison.OrdinalIgnoreCase));

            if (normalizedStatus == null)
            {
                return BadRequest(new
                {
                    success = false,
                    message = "Invalid quotation status.",
                    allowedStatuses = AllowedStatuses
                });
            }

            if (request.QuotedAmount.HasValue &&
                request.QuotedAmount.Value < 0)
            {
                return BadRequest(new
                {
                    success = false,
                    message = "Quoted amount cannot be negative."
                });
            }

            if (normalizedStatus == "Quoted" &&
                (!request.QuotedAmount.HasValue ||
                 request.QuotedAmount.Value <= 0))
            {
                return BadRequest(new
                {
                    success = false,
                    message = "A positive quoted amount is required when status is Quoted."
                });
            }

            var employeeIdValue =
                User.FindFirstValue(ClaimTypes.NameIdentifier);

            if (!int.TryParse(employeeIdValue, out var employeeId))
            {
                return Unauthorized(new
                {
                    success = false,
                    message = "Unable to identify the authenticated employee."
                });
            }

            var employeeIsActive = await _context.Employees
                .AsNoTracking()
                .AnyAsync(e =>
                    e.EmployeeID == employeeId &&
                    e.EmployeeStatus == "Active" &&
                    (e.Role == "ProductionManager" ||
                     e.Role == "Admin"));

            if (!employeeIsActive)
            {
                return StatusCode(403, new
                {
                    success = false,
                    message = "Only active Production Managers and Admins can review quotations."
                });
            }

            var quotation = await _context.CustomerQuotations
                .FirstOrDefaultAsync(q => q.QuotationID == id);

            if (quotation == null)
            {
                return NotFound(new
                {
                    success = false,
                    message = "Customer quotation was not found."
                });
            }

            quotation.Status = normalizedStatus;

            quotation.ManagerNotes =
                string.IsNullOrWhiteSpace(request.ManagerNotes)
                    ? null
                    : request.ManagerNotes.Trim();

            quotation.QuotedAmount = request.QuotedAmount;

            quotation.ReviewedByEmployeeID = employeeId;

            quotation.ReviewedAtUtc = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            return Ok(new
            {
                success = true,
                message = "Customer quotation reviewed successfully.",
                quotationID = quotation.QuotationID,
                referenceNumber = quotation.ReferenceNumber,
                status = quotation.Status,
                quotedAmount = quotation.QuotedAmount,
                reviewedByEmployeeID = quotation.ReviewedByEmployeeID,
                reviewedAtUtc = quotation.ReviewedAtUtc
            });
        }
    }

    // ============================================================
    // REQUEST DTO - PUBLIC CUSTOMER SUBMISSION
    // ============================================================

    public class CreateCustomerQuotationRequest
    {
        [Required]
        [StringLength(100, MinimumLength = 2)]
        public string FullName { get; set; } = string.Empty;

        [StringLength(150)]
        public string? Company { get; set; }

        [Required]
        [EmailAddress]
        [StringLength(254)]
        public string Email { get; set; } = string.Empty;

        [Required]
        [Phone]
        [StringLength(30)]
        public string Phone { get; set; } = string.Empty;

        [Required]
        [StringLength(100)]
        public string Category { get; set; } = string.Empty;

        [Range(1, 1000000)]
        public int Quantity { get; set; }

        [Required]
        public DateTime Deadline { get; set; }

        [Required]
        [StringLength(10000, MinimumLength = 10)]
        public string Specifications { get; set; } = string.Empty;
    }

    // ============================================================
    // REQUEST DTO - MANAGER REVIEW
    // ============================================================

    public class ReviewCustomerQuotationRequest
    {
        [Required]
        public string Status { get; set; } = string.Empty;

        [StringLength(10000)]
        public string? ManagerNotes { get; set; }

        [Range(typeof(decimal), "0", "9999999999999999.99", ParseLimitsInInvariantCulture = true)]
        public decimal? QuotedAmount { get; set; }
    }
}
