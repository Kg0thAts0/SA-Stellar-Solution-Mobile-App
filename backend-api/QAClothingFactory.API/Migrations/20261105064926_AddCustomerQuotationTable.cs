using System;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QAClothingFactory.API.Migrations
{
    /// <summary>
    /// Creates the customer quotation table for requests
    /// submitted through the QA Clothing Factory website.
    ///
    /// GoogleSubjectId and its unique index already exist
    /// in the Aiven database and are intentionally untouched.
    /// </summary>
    public partial class AddCustomerQuotationTable : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            // ====================================================
            // CREATE CUSTOMER QUOTATION TABLE
            // ====================================================

            migrationBuilder.CreateTable(
                name: "customer_quotation",
                columns: table => new
                {
                    QuotationID = table.Column<int>(
                        type: "int",
                        nullable: false)
                        .Annotation(
                            "MySql:ValueGenerationStrategy",
                            MySqlValueGenerationStrategy.IdentityColumn),

                    ReferenceNumber = table.Column<string>(
                        type: "varchar(50)",
                        maxLength: 50,
                        nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    FullName = table.Column<string>(
                        type: "varchar(100)",
                        maxLength: 100,
                        nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    Company = table.Column<string>(
                        type: "varchar(150)",
                        maxLength: 150,
                        nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    Email = table.Column<string>(
                        type: "varchar(254)",
                        maxLength: 254,
                        nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    Phone = table.Column<string>(
                        type: "varchar(30)",
                        maxLength: 30,
                        nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    Category = table.Column<string>(
                        type: "varchar(100)",
                        maxLength: 100,
                        nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    Quantity = table.Column<int>(
                        type: "int",
                        nullable: false),

                    Deadline = table.Column<DateTime>(
                        type: "datetime(6)",
                        nullable: false),

                    Specifications = table.Column<string>(
                        type: "longtext",
                        nullable: false)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    Status = table.Column<string>(
                        type: "varchar(30)",
                        maxLength: 30,
                        nullable: false,
                        defaultValue: "Pending")
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    CreatedAtUtc = table.Column<DateTime>(
                        type: "datetime(6)",
                        nullable: false),

                    ReviewedByEmployeeID = table.Column<int>(
                        type: "int",
                        nullable: true),

                    ReviewedAtUtc = table.Column<DateTime>(
                        type: "datetime(6)",
                        nullable: true),

                    ManagerNotes = table.Column<string>(
                        type: "longtext",
                        nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4"),

                    QuotedAmount = table.Column<decimal>(
                        type: "decimal(18,2)",
                        precision: 18,
                        scale: 2,
                        nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey(
                        "PK_customer_quotation",
                        x => x.QuotationID);

                    table.ForeignKey(
                        name: "FK_customer_quotation_employee_ReviewedByEmployeeID",
                        column: x => x.ReviewedByEmployeeID,
                        principalTable: "employee",
                        principalColumn: "EmployeeID",
                        onDelete: ReferentialAction.SetNull);
                })
                .Annotation("MySql:CharSet", "utf8mb4");

            // ====================================================
            // CREATE CUSTOMER QUOTATION INDEXES
            // ====================================================

            migrationBuilder.CreateIndex(
                name: "IX_customer_quotation_CreatedAtUtc",
                table: "customer_quotation",
                column: "CreatedAtUtc");

            migrationBuilder.CreateIndex(
                name: "IX_customer_quotation_ReferenceNumber",
                table: "customer_quotation",
                column: "ReferenceNumber",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_customer_quotation_ReviewedByEmployeeID",
                table: "customer_quotation",
                column: "ReviewedByEmployeeID");

            migrationBuilder.CreateIndex(
                name: "IX_customer_quotation_Status",
                table: "customer_quotation",
                column: "Status");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            // ====================================================
            // ROLLBACK CUSTOMER QUOTATION TABLE ONLY
            //
            // IMPORTANT:
            // Do not remove GoogleSubjectId or its index.
            // They belong to the existing Google SSO feature.
            // ====================================================

            migrationBuilder.DropTable(
                name: "customer_quotation");
        }
    }
}
