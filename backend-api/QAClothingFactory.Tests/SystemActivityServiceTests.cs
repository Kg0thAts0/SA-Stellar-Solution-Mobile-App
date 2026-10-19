
using System;
using System.Linq;
using System.Net;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Services;
using Xunit;

namespace QAClothingFactory.Tests
{
    public class SystemActivityServiceTests
    {
        private static ApplicationDbContext CreateDbContext()
        {
            var options =
                new DbContextOptionsBuilder<ApplicationDbContext>()
                    .UseInMemoryDatabase(Guid.NewGuid().ToString())
                    .Options;

            return new ApplicationDbContext(options);
        }

        private static SystemActivityService CreateService(
            ApplicationDbContext context,
            HttpContext? httpContext = null)
        {
            var accessor = new HttpContextAccessor
            {
                HttpContext = httpContext
            };

            return new SystemActivityService(context, accessor);
        }

        [Fact]
        public async Task LogActivityAsync_ValidData_SavesActivity()
        {
            // Arrange
            using var context = CreateDbContext();
            var service = CreateService(context);

            // Act
            await service.LogActivityAsync(
                "CREATE",
                "Created a production batch",
                "ProductionBatch",
                101
            );

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.Equal("CREATE", activity.ActionType);
            Assert.Equal(
                "Created a production batch",
                activity.Description
            );
            Assert.Equal("ProductionBatch", activity.EntityType);
            Assert.Equal(101, activity.EntityID);
        }

        [Fact]
        public async Task LogActivityAsync_AuthenticatedEmployee_SavesEmployeeId()
        {
            // Arrange
            using var context = CreateDbContext();

            var claims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, "25")
            };

            var identity = new ClaimsIdentity(
                claims,
                "TestAuthentication"
            );

            var httpContext = new DefaultHttpContext
            {
                User = new ClaimsPrincipal(identity)
            };

            var service = CreateService(context, httpContext);

            // Act
            await service.LogActivityAsync(
                "LOGIN",
                "Employee logged in"
            );

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.Equal(25, activity.EmployeeID);
        }

        [Fact]
        public async Task LogActivityAsync_ValidIpAddress_SavesIpAddress()
        {
            // Arrange
            using var context = CreateDbContext();

            var httpContext = new DefaultHttpContext();

            httpContext.Connection.RemoteIpAddress =
                IPAddress.Parse("192.168.1.10");

            var service = CreateService(context, httpContext);

            // Act
            await service.LogActivityAsync(
                "UPDATE",
                "Updated inventory"
            );

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.Equal("192.168.1.10", activity.IPAddress);
        }

        [Fact]
        public async Task LogActivityAsync_TextWithSpaces_TrimsValues()
        {
            // Arrange
            using var context = CreateDbContext();
            var service = CreateService(context);

            // Act
            await service.LogActivityAsync(
                "  CREATE  ",
                "  Created material  ",
                "  RawMaterial  ",
                15
            );

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.Equal("CREATE", activity.ActionType);
            Assert.Equal("Created material", activity.Description);
            Assert.Equal("RawMaterial", activity.EntityType);
            Assert.Equal(15, activity.EntityID);
        }

        [Fact]
        public async Task LogActivityAsync_NoHttpContext_SavesNullEmployeeAndIp()
        {
            // Arrange
            using var context = CreateDbContext();
            var service = CreateService(context);

            // Act
            await service.LogActivityAsync(
                "SYSTEM",
                "Automated system operation"
            );

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.Null(activity.EmployeeID);
            Assert.Null(activity.IPAddress);
        }

        [Fact]
        public async Task LogActivityAsync_InvalidEmployeeClaim_SavesNullEmployeeId()
        {
            // Arrange
            using var context = CreateDbContext();

            var claims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, "invalid-id")
            };

            var httpContext = new DefaultHttpContext
            {
                User = new ClaimsPrincipal(
                    new ClaimsIdentity(claims, "TestAuthentication")
                )
            };

            var service = CreateService(context, httpContext);

            // Act
            await service.LogActivityAsync(
                "VIEW",
                "Viewed production report"
            );

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.Null(activity.EmployeeID);
        }

        [Fact]
        public async Task LogActivityAsync_EmptyOptionalEntityType_SavesNull()
        {
            // Arrange
            using var context = CreateDbContext();
            var service = CreateService(context);

            // Act
            await service.LogActivityAsync(
                "VIEW",
                "Viewed dashboard",
                "   "
            );

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.Null(activity.EntityType);
        }

        [Fact]
        public async Task LogActivityAsync_ValidActivity_SetsActivityTime()
        {
            // Arrange
            using var context = CreateDbContext();
            var service = CreateService(context);

            var before = DateTime.Now;

            // Act
            await service.LogActivityAsync(
                "CREATE",
                "Created inventory transaction"
            );

            var after = DateTime.Now;

            // Assert
            var activity = await context.SystemActivities.SingleAsync();

            Assert.InRange(activity.ActivityTime, before, after);
        }
    }
}
