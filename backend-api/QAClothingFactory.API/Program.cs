using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Services;
using System.Net;
using System.Text;
using System.Threading.RateLimiting;

var builder = WebApplication.CreateBuilder(args);

// ============================================================
// CONTROLLERS
// ============================================================

builder.Services.AddControllers();

// ============================================================
// OPENAPI
// ============================================================

builder.Services.AddOpenApi();

// ============================================================
// CORS - CUSTOMER WEBSITE
// ============================================================

const string CustomerWebsiteCorsPolicy = "CustomerWebsiteCorsPolicy";

builder.Services.AddCors(options =>
{
    options.AddPolicy(CustomerWebsiteCorsPolicy, policy =>
    {
        policy
            .WithOrigins(
                "https://qa-clothing-factory-customer.onrender.com"
            )
            .AllowAnyHeader()
            .AllowAnyMethod();
    });
});

// ============================================================
// DATABASE CONNECTION
// ============================================================

var connectionString =
    builder.Configuration.GetConnectionString(
        "DefaultConnection"
    );

if (string.IsNullOrWhiteSpace(connectionString))
{
    throw new InvalidOperationException(
        "Database connection string 'DefaultConnection' was not found."
    );
}

// ============================================================
// ENTITY FRAMEWORK + POSTGRESQL (SUPABASE)
// ============================================================

builder.Services.AddDbContext<ApplicationDbContext>(
    options => options.UseNpgsql(connectionString)
);

// ============================================================
// HTTP CONTEXT ACCESSOR
// ============================================================

builder.Services.AddHttpContextAccessor();

// ============================================================
// EXISTING APPLICATION SERVICES
// ============================================================

builder.Services.AddScoped<TokenService>();
builder.Services.AddScoped<SystemActivityService>();
builder.Services.AddScoped<GmailEmailService>();
builder.Services.AddScoped<CustomerTrackingPinService>();

// ============================================================
// PUBLIC CUSTOMER RATE LIMITING
// ============================================================

builder.Services.AddRateLimiter(options =>
{
    options.RejectionStatusCode =
        StatusCodes.Status429TooManyRequests;

    options.AddPolicy("CustomerPublic", context =>
    {
        var clientAddress =
            context.Connection.RemoteIpAddress?.ToString()
            ?? "unknown";

        return RateLimitPartition.GetFixedWindowLimiter(
            partitionKey: clientAddress,
            factory: _ => new FixedWindowRateLimiterOptions
            {
                PermitLimit = 10,
                Window = TimeSpan.FromMinutes(1),
                QueueLimit = 0,
                AutoReplenishment = true
            }
        );
    });

    options.AddPolicy("CustomerVerification", context =>
    {
        var clientAddress =
            context.Connection.RemoteIpAddress?.ToString()
            ?? "unknown";

        return RateLimitPartition.GetFixedWindowLimiter(
            partitionKey: clientAddress,
            factory: _ => new FixedWindowRateLimiterOptions
            {
                PermitLimit = 5,
                Window = TimeSpan.FromMinutes(5),
                QueueLimit = 0,
                AutoReplenishment = true
            }
        );
    });
});

// ============================================================
// JWT CONFIGURATION
// ============================================================

var jwtKey = builder.Configuration["Jwt:Key"];
var jwtIssuer = builder.Configuration["Jwt:Issuer"];
var jwtAudience = builder.Configuration["Jwt:Audience"];

if (string.IsNullOrWhiteSpace(jwtKey))
{
    throw new InvalidOperationException(
        "JWT signing key is missing."
    );
}

builder.Services
    .AddAuthentication(
        JwtBearerDefaults.AuthenticationScheme
    )
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters =
            new TokenValidationParameters
            {
                ValidateIssuer = true,
                ValidateAudience = true,
                ValidateLifetime = true,
                ValidateIssuerSigningKey = true,

                ValidIssuer = jwtIssuer,
                ValidAudience = jwtAudience,

                IssuerSigningKey =
                    new SymmetricSecurityKey(
                        Encoding.UTF8.GetBytes(jwtKey)
                    ),

                ClockSkew = TimeSpan.Zero
            };
    });

// ============================================================
// AUTHORIZATION
// ============================================================

builder.Services.AddAuthorization();

// ============================================================
// BUILD APPLICATION
// ============================================================

var app = builder.Build();

// ============================================================
// DEVELOPMENT
// ============================================================

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();

    // ========================================================
    // TEMPORARY SUPABASE DATABASE CONNECTION TEST
    //
    // Development only.
    // Local requests only.
    // Read-only SQL.
    // Remove after successful verification.
    // ========================================================

    app.MapGet(
        "/dev/database-check",
        async (
            HttpContext httpContext,
            ApplicationDbContext db,
            ILogger<Program> logger,
            CancellationToken cancellationToken
        ) =>
        {
            // Restrict the endpoint to local requests.
            var remoteAddress =
                httpContext.Connection.RemoteIpAddress;

            if (remoteAddress is null ||
                !IPAddress.IsLoopback(remoteAddress))
            {
                return Results.StatusCode(
                    StatusCodes.Status403Forbidden
                );
            }

            try
            {
                var connection =
                    db.Database.GetDbConnection();

                await db.Database.OpenConnectionAsync(
                    cancellationToken
                );

                try
                {
                    await using var command =
                        connection.CreateCommand();

                    command.CommandText =
                        "SELECT COUNT(*) FROM public.employee";

                    var result =
                        await command.ExecuteScalarAsync(
                            cancellationToken
                        );

                    var employeeCount =
                        Convert.ToInt64(result);

                    return Results.Ok(new
                    {
                        status = "connected",
                        database = "PostgreSQL",
                        employeeTableAccessible = true,
                        employeeCount
                    });
                }
                finally
                {
                    await db.Database.CloseConnectionAsync();
                }
            }
            catch (Exception exception)
            {
                logger.LogError(
                    exception,
                    "PostgreSQL database connection test failed."
                );

                return Results.Json(
                    new
                    {
                        status = "failed",
                        message =
                            "Database connectivity test failed. " +
                            "Check the API terminal for details."
                    },
                    statusCode:
                        StatusCodes.Status503ServiceUnavailable
                );
            }
        }
    );
}

// ============================================================
// HTTPS
// ============================================================

app.UseHttpsRedirection();

// ============================================================
// CORS
// ============================================================

app.UseCors(CustomerWebsiteCorsPolicy);

// ============================================================
// RATE LIMITING
// ============================================================

app.UseRateLimiter();

// ============================================================
// AUTHENTICATION
// ============================================================

app.UseAuthentication();
app.UseAuthorization();

// ============================================================
// CONTROLLERS
// ============================================================

app.MapControllers();

// ============================================================
// HEALTH CHECK
// ============================================================

app.MapGet("/healthz", () =>
    Results.Ok(new { status = "healthy" })
);

// ============================================================
// START APPLICATION
// ============================================================

app.Run();
