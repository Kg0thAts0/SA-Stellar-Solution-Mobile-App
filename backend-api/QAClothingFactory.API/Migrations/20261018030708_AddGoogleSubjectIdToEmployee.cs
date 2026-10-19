using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QAClothingFactory.API.Migrations
{
    public partial class AddGoogleSubjectIdToEmployee : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            // =============================================
            // ADD GOOGLE ACCOUNT IDENTIFIER
            // =============================================

            migrationBuilder.AddColumn<string>(
                name: "GoogleSubjectId",
                table: "employee",
                type: "varchar(255)",
                maxLength: 255,
                nullable: true
            );

            // =============================================
            // ENSURE GOOGLE ACCOUNT UNIQUENESS
            // =============================================

            migrationBuilder.CreateIndex(
                name: "UX_employee_GoogleSubjectId",
                table: "employee",
                column: "GoogleSubjectId",
                unique: true
            );
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            // =============================================
            // REMOVE GOOGLE ACCOUNT INDEX
            // =============================================

            migrationBuilder.DropIndex(
                name: "UX_employee_GoogleSubjectId",
                table: "employee"
            );

            // =============================================
            // REMOVE GOOGLE ACCOUNT COLUMN
            // =============================================

            migrationBuilder.DropColumn(
                name: "GoogleSubjectId",
                table: "employee"
            );
        }
    }
}
