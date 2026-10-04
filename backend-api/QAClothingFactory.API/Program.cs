using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Services;
using System.Text;

var builder =
    WebApplication.CreateBuilder(args);


// ============================================================
// CONTROLLERS
// ============================================================

builder.Services.AddControllers();


// ============================================================
// OPENAPI
// ============================================================

builder.Services.AddOpenApi();


// ============================================================
// DATABASE CONNECTION
// ============================================================

var connectionString =
    builder.Configuration.GetConnectionString(
        "DefaultConnection"
    );


if (
    string.IsNullOrWhiteSpace(
        connectionString
    )
)
{
    throw new InvalidOperationException(
        "Database connection string 'DefaultConnection' was not found."
    );
}


// ============================================================
// ENTITY FRAMEWORK + MARIADB
// ============================================================

builder.Services.AddDbContext<ApplicationDbContext>(
    options =>
        options.UseMySql(
            connectionString,

            ServerVersion.AutoDetect(
                connectionString
            )
        )
);


// ============================================================
// HTTP CONTEXT ACCESSOR
// ============================================================
//
// Required by SystemActivityService so it can read:
// - authenticated EmployeeID from JWT
// - request IP address
//
// ============================================================

builder.Services.AddHttpContextAccessor();


// ============================================================
// APPLICATION SERVICES
// ============================================================

builder.Services.AddScoped<TokenService>();

builder.Services.AddScoped<SystemActivityService>();


// ============================================================
// JWT CONFIGURATION
// ============================================================

var jwtKey =
    builder.Configuration[
        "Jwt:Key"
    ];

var jwtIssuer =
    builder.Configuration[
        "Jwt:Issuer"
    ];

var jwtAudience =
    builder.Configuration[
        "Jwt:Audience"
    ];


if (
    string.IsNullOrWhiteSpace(
        jwtKey
    )
)
{
    throw new InvalidOperationException(
        "JWT signing key is missing."
    );
}


builder.Services
    .AddAuthentication(
        JwtBearerDefaults.AuthenticationScheme
    )
    .AddJwtBearer(
        options =>
        {
            options.TokenValidationParameters =
                new TokenValidationParameters
                {
                    ValidateIssuer =
                        true,

                    ValidateAudience =
                        true,

                    ValidateLifetime =
                        true,

                    ValidateIssuerSigningKey =
                        true,

                    ValidIssuer =
                        jwtIssuer,

                    ValidAudience =
                        jwtAudience,

                    IssuerSigningKey =
                        new SymmetricSecurityKey(
                            Encoding.UTF8.GetBytes(
                                jwtKey
                            )
                        ),

                    ClockSkew =
                        TimeSpan.Zero
                };
        }
    );


// ============================================================
// AUTHORIZATION
// ============================================================

builder.Services.AddAuthorization();


// ============================================================
// BUILD APPLICATION
// ============================================================

var app =
    builder.Build();


// ============================================================
// DEVELOPMENT
// ============================================================

if (
    app.Environment.IsDevelopment()
)
{
    app.MapOpenApi();
}


// ============================================================
// HTTPS
// ============================================================

app.UseHttpsRedirection();


// ============================================================
// AUTHENTICATION
// ============================================================
//
// Authentication MUST run before authorization.
//
// ============================================================

app.UseAuthentication();

app.UseAuthorization();


// ============================================================
// CONTROLLERS
// ============================================================

app.MapControllers();


// ============================================================
// START APPLICATION
// ============================================================

app.Run();