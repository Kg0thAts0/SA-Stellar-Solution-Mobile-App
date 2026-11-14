using System.ComponentModel.DataAnnotations;
using System.Security.Claims;
using Microsoft.AspNetCore.RateLimiting;
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
        private readonly QAClothingFactory.API.Services.CustomerTrackingPinService _pinService;

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
            ApplicationDbContext context,
            QAClothingFactory.API.Services.CustomerTrackingPinService pinService)
        {
            _context = context;
            _pinService = pinService;
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

            var trackingPin = _pinService.GeneratePin();

            var quotation = new CustomerQuotation
            {
                ReferenceNumber =
                    "QA-" +
                    DateTime.UtcNow.ToString("yyyyMMdd") +
                    "-" +
                    Guid.NewGuid().ToString("N")[..12].ToUpperInvariant(),

                TrackingPinHash = _pinService.HashPin(trackingPin),

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
                trackingPin,
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

            if (quotation.CustomerDecision != "Pending")
            {
                return Conflict(new
                {
                    success = false,
                    message = "A customer decision has already been recorded. The quotation cannot be changed."
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

        // ========================================================
        // 5. CUSTOMER - TRACK USING REFERENCE + PRIVATE PIN
        // POST /api/CustomerQuotations/track
        // ========================================================
        [HttpPost("track")]
        [AllowAnonymous]
        [EnableRateLimiting("CustomerVerification")]
        public async Task<IActionResult> TrackQuotation(
            [FromBody] CustomerTrackingRequest request,
            CancellationToken cancellationToken)
        {
            var quotation = await FindVerifiedQuotationAsync(
                request.ReferenceNumber, request.TrackingPin, cancellationToken);

            if (quotation is null)
                return Unauthorized(new
                {
                    success = false,
                    message = "Invalid tracking details or tracking is unavailable for this quotation."
                });

            var history = await _context.CustomerOrderStatusHistories
                .AsNoTracking()
                .Where(h => h.QuotationID == quotation.QuotationID && h.IsVisibleToCustomer)
                .OrderBy(h => h.CreatedAtUtc)
                .Select(h => new
                {
                    h.Status,
                    h.Description,
                    h.CreatedAtUtc
                })
                .ToListAsync(cancellationToken);

            return Ok(new
            {
                success = true,
                quotation = new
                {
                    quotation.ReferenceNumber,
                    quotation.Category,
                    quotation.Quantity,
                    quotation.Deadline,
                    quotation.Status,
                    quotation.QuotedAmount,
                    quotation.CustomerDecision,
                    quotation.CustomerDecisionAtUtc,
                    quotation.CreatedAtUtc
                },
                history
            });
        }

        // ========================================================
        // 6. CUSTOMER - ACCEPT OR REJECT A QUOTED OFFER
        // POST /api/CustomerQuotations/decision
        // ========================================================
        [HttpPost("decision")]
        [AllowAnonymous]
        [EnableRateLimiting("CustomerVerification")]
        public async Task<IActionResult> DecideQuotation(
            [FromBody] CustomerQuotationDecisionRequest request,
            CancellationToken cancellationToken)
        {
            var decision = request.Decision.Trim();
            if (decision != "Accepted" && decision != "Rejected")
                return BadRequest(new
                {
                    success = false,
                    message = "Decision must be Accepted or Rejected."
                });

            var quotation = await FindVerifiedQuotationAsync(
                request.ReferenceNumber, request.TrackingPin, cancellationToken);

            if (quotation is null)
                return Unauthorized(new
                {
                    success = false,
                    message = "Invalid tracking details or tracking is unavailable for this quotation."
                });

            // Atomic update prevents simultaneous requests from overwriting
            // an earlier customer decision.
            var now = DateTime.UtcNow;
            var updated = await _context.CustomerQuotations
                .Where(q => q.QuotationID == quotation.QuotationID
                    && q.Status == "Quoted"
                    && q.QuotedAmount != null
                    && q.QuotedAmount > 0
                    && q.CustomerDecision == "Pending")
                .ExecuteUpdateAsync(setters => setters
                    .SetProperty(q => q.CustomerDecision, decision)
                    .SetProperty(q => q.CustomerDecisionAtUtc, now),
                    cancellationToken);

            if (updated != 1)
                return Conflict(new
                {
                    success = false,
                    message = "This quotation is not available for a new decision."
                });

            // Tracking reflects the decision; no production status is invented.
            _context.CustomerOrderStatusHistories.Add(
                new CustomerOrderStatusHistory
                {
                    QuotationID = quotation.QuotationID,
                    Status = decision == "Accepted"
                        ? "Quotation Accepted" : "Quotation Rejected",
                    Description = decision == "Accepted"
                        ? "Customer accepted the quoted amount."
                        : "Customer rejected the quoted amount.",
                    CreatedAtUtc = now,
                    IsVisibleToCustomer = true
                });

            await _context.SaveChangesAsync(cancellationToken);

            return Ok(new
            {
                success = true,
                referenceNumber = quotation.ReferenceNumber,
                customerDecision = decision,
                customerDecisionAtUtc = now
            });
        }

        private async Task<CustomerQuotation?> FindVerifiedQuotationAsync(
            string referenceNumber,
            string trackingPin,
            CancellationToken cancellationToken)
        {
            if (string.IsNullOrWhiteSpace(referenceNumber) ||
                referenceNumber.Length > 50 ||
                !_pinService.IsValidPin(trackingPin))
                return null;

            var quotation = await _context.CustomerQuotations
                .AsNoTracking()
                .FirstOrDefaultAsync(
                    q => q.ReferenceNumber == referenceNumber.Trim(),
                    cancellationToken);

            // Legacy quotations without a PIN are intentionally inaccessible.
            if (quotation is null ||
                !_pinService.VerifyPin(trackingPin, quotation.TrackingPinHash))
                return null;

            return quotation;
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


public class CustomerTrackingRequest
{
    [Required, StringLength(50, MinimumLength = 5)]
    public string ReferenceNumber { get; set; } = string.Empty;

    [Required, RegularExpression("^[0-9]{12}$")]
    public string TrackingPin { get; set; } = string.Empty;
}

public sealed class CustomerQuotationDecisionRequest : CustomerTrackingRequest
{
    [Required]
    public string Decision { get; set; } = string.Empty;
}
