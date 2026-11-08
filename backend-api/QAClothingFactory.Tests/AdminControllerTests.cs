using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Controllers;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Services;
using Xunit;

namespace QAClothingFactory.Tests
{
    public class AdminControllerTests
    {
        [Fact]
        public async Task ApproveEmployee_EmployeeNotFound_ReturnsNotFound()
        {
            // Arrange
            var options = new DbContextOptionsBuilder<ApplicationDbContext>()
                .UseInMemoryDatabase(Guid.NewGuid().ToString())
                .Options;

            using var context = new ApplicationDbContext(options);

            var accessor = new HttpContextAccessor
            {
                HttpContext = new DefaultHttpContext()
            };

            var activityService = new SystemActivityService(context, accessor);
            var controller = new AdminController(context, activityService);

            // Act
            var result = await controller.ApproveEmployee(999);

            // Assert
            Assert.IsType<NotFoundObjectResult>(result);
        }
    }
}
