using System;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QAClothingFactory.API.Migrations
{
    /// <inheritdoc />
    public partial class AddMaterialRequestWorkflow : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "material_request",
                columns: table => new
                {
                    RequestID = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySql:ValueGenerationStrategy", MySqlValueGenerationStrategy.IdentityColumn),
                    BatchID = table.Column<int>(type: "int", nullable: false),
                    RequestedBy = table.Column<int>(type: "int", nullable: false),
                    RequestedAt = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    Status = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false, defaultValue: "Pending")
                        .Annotation("MySql:CharSet", "utf8mb4"),
                    Notes = table.Column<string>(type: "longtext", nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4")
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_material_request", x => x.RequestID);
                    table.ForeignKey(
                        name: "FK_material_request_employee_RequestedBy",
                        column: x => x.RequestedBy,
                        principalTable: "employee",
                        principalColumn: "EmployeeID",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_material_request_production_batch_BatchID",
                        column: x => x.BatchID,
                        principalTable: "production_batch",
                        principalColumn: "BatchID",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "material_request_item",
                columns: table => new
                {
                    RequestItemID = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySql:ValueGenerationStrategy", MySqlValueGenerationStrategy.IdentityColumn),
                    RequestID = table.Column<int>(type: "int", nullable: false),
                    MaterialID = table.Column<int>(type: "int", nullable: false),
                    QuantityRequested = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false),
                    QuantityIssued = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false, defaultValue: 0m),
                    Notes = table.Column<string>(type: "longtext", nullable: true)
                        .Annotation("MySql:CharSet", "utf8mb4")
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_material_request_item", x => x.RequestItemID);
                    table.ForeignKey(
                        name: "FK_material_request_item_material_request_RequestID",
                        column: x => x.RequestID,
                        principalTable: "material_request",
                        principalColumn: "RequestID",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_material_request_item_raw_material_MaterialID",
                        column: x => x.MaterialID,
                        principalTable: "raw_material",
                        principalColumn: "MaterialID",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "IX_material_request_BatchID",
                table: "material_request",
                column: "BatchID");

            migrationBuilder.CreateIndex(
                name: "IX_material_request_RequestedAt",
                table: "material_request",
                column: "RequestedAt");

            migrationBuilder.CreateIndex(
                name: "IX_material_request_RequestedBy",
                table: "material_request",
                column: "RequestedBy");

            migrationBuilder.CreateIndex(
                name: "IX_material_request_Status",
                table: "material_request",
                column: "Status");

            migrationBuilder.CreateIndex(
                name: "IX_material_request_item_MaterialID",
                table: "material_request_item",
                column: "MaterialID");

            migrationBuilder.CreateIndex(
                name: "IX_material_request_item_RequestID",
                table: "material_request_item",
                column: "RequestID");

            migrationBuilder.CreateIndex(
                name: "IX_material_request_item_RequestID_MaterialID",
                table: "material_request_item",
                columns: new[] { "RequestID", "MaterialID" },
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "material_request_item");

            migrationBuilder.DropTable(
                name: "material_request");
        }
    }
}
