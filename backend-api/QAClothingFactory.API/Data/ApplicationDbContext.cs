using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Models;

namespace QAClothingFactory.API.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(
            DbContextOptions<ApplicationDbContext> options
        ) : base(options)
        {
        }


        // ====================================================
        // EXISTING DATABASE TABLES
        // ====================================================

        public DbSet<Employee> Employees { get; set; }

        public DbSet<FinishedGood> FinishedGoods { get; set; }

        public DbSet<RawMaterial> RawMaterials { get; set; }

        public DbSet<InventoryTransaction> InventoryTransactions { get; set; }

        public DbSet<ProductionBatch> ProductionBatches { get; set; }

        public DbSet<LoginAttempt> LoginAttempts { get; set; }

        public DbSet<ShiftTeam> ShiftTeams { get; set; }


        // ====================================================
        // DATABASE MAPPING
        // ====================================================

        protected override void OnModelCreating(
            ModelBuilder modelBuilder
        )
        {
            base.OnModelCreating(modelBuilder);


            // =================================================
            // EMPLOYEE
            // =================================================

            modelBuilder.Entity<Employee>(entity =>
            {
                entity.ToTable("employee");

                entity.HasKey(e => e.EmployeeID);

                entity.Property(e => e.EmployeeID)
                    .HasColumnName("EmployeeID");

                entity.Property(e => e.FullName)
                    .HasColumnName("FullName")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.EmailAddress)
                    .HasColumnName("EmailAddress")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.HasIndex(e => e.EmailAddress)
                    .IsUnique();

                entity.Property(e => e.Password)
                    .HasColumnName("Password")
                    .HasMaxLength(255)
                    .IsRequired();

                entity.Property(e => e.Role)
                    .HasColumnName("Role")
                    .HasColumnType(
                        "enum('Admin','ProductionManager','QualityController','InventoryClerk','Supervisor')"
                    )
                    .IsRequired();

                entity.Property(e => e.EmployeeStatus)
                    .HasColumnName("EmployeeStatus")
                    .HasColumnType(
                        "enum('Pending','Active','Inactive')"
                    );

                entity.Property(e => e.CreatedAt)
                    .HasColumnName("CreatedAt");

                entity.Property(e => e.CreatedBy)
                    .HasColumnName("CreatedBy");
            });


            // =================================================
            // FINISHED GOOD
            // =================================================

            modelBuilder.Entity<FinishedGood>(entity =>
            {
                entity.ToTable("finished_good");

                entity.HasKey(e => e.ProductID);

                entity.Property(e => e.ProductID)
                    .HasColumnName("ProductID");

                entity.Property(e => e.ProductCode)
                    .HasColumnName("ProductCode")
                    .HasMaxLength(50)
                    .IsRequired();

                entity.HasIndex(e => e.ProductCode)
                    .IsUnique();

                entity.Property(e => e.ProductName)
                    .HasColumnName("ProductName")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.Category)
                    .HasColumnName("Category")
                    .HasMaxLength(50);

                entity.Property(e => e.Unit)
                    .HasColumnName("Unit")
                    .HasMaxLength(20)
                    .IsRequired();

                entity.Property(e => e.CurrentStock)
                    .HasColumnName("CurrentStock")
                    .HasPrecision(10, 2);

                entity.Property(e => e.UnitPrice)
                    .HasColumnName("UnitPrice")
                    .HasPrecision(10, 2);

                entity.Property(e => e.ProductionBatchID)
                    .HasColumnName("ProductionBatchID")
                    .HasMaxLength(50);

                entity.Property(e => e.BatchID)
                    .HasColumnName("BatchID");

                entity.Property(e => e.QualityStatus)
                    .HasColumnName("QualityStatus")
                    .HasColumnType(
                        "enum('Pending','Approved','Rejected')"
                    );

                entity.Property(e => e.QualityInspectorID)
                    .HasColumnName("QualityInspectorID");

                entity.Property(e => e.ProductionDate)
                    .HasColumnName("ProductionDate");

                entity.Property(e => e.RecordedBy)
                    .HasColumnName("RecordedBy");

                entity.Property(e => e.RecordedAt)
                    .HasColumnName("RecordedAt");

                entity.Property(e => e.InspectionNotes)
                    .HasColumnName("InspectionNotes");
            });


            // =================================================
            // RAW MATERIAL
            // =================================================

            modelBuilder.Entity<RawMaterial>(entity =>
            {
                entity.ToTable("raw_material");

                entity.HasKey(e => e.MaterialID);

                entity.Property(e => e.MaterialID)
                    .HasColumnName("MaterialID");

                entity.Property(e => e.MaterialCode)
                    .HasColumnName("MaterialCode")
                    .HasMaxLength(50)
                    .IsRequired();

                entity.HasIndex(e => e.MaterialCode)
                    .IsUnique();

                entity.Property(e => e.MaterialName)
                    .HasColumnName("MaterialName")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.Category)
                    .HasColumnName("Category")
                    .HasMaxLength(50);

                entity.Property(e => e.Unit)
                    .HasColumnName("Unit")
                    .HasMaxLength(20)
                    .IsRequired();

                entity.Property(e => e.CurrentStock)
                    .HasColumnName("CurrentStock")
                    .HasPrecision(10, 2);

                entity.Property(e => e.MinimumStock)
                    .HasColumnName("MinimumStock")
                    .HasPrecision(10, 2);

                entity.Property(e => e.ReorderLevel)
                    .HasColumnName("ReorderLevel")
                    .HasPrecision(10, 2);

                entity.Property(e => e.UnitCost)
                    .HasColumnName("UnitCost")
                    .HasPrecision(10, 2);

                entity.Property(e => e.SupplierInfo)
                    .HasColumnName("SupplierInfo");

                entity.Property(e => e.LastUpdated)
                    .HasColumnName("LastUpdated");

                entity.Property(e => e.UpdatedBy)
                    .HasColumnName("UpdatedBy");
            });


            // =================================================
            // INVENTORY TRANSACTION
            // =================================================

            modelBuilder.Entity<InventoryTransaction>(entity =>
            {
                entity.ToTable(
                    "inventory_transaction"
                );

                entity.HasKey(
                    e => e.TransactionID
                );

                entity.Property(e => e.TransactionID)
                    .HasColumnName("TransactionID");

                entity.Property(e => e.MaterialID)
                    .HasColumnName("MaterialID");

                entity.Property(e => e.ProductID)
                    .HasColumnName("ProductID");

                entity.Property(e => e.TransactionType)
                    .HasColumnName("TransactionType")
                    .HasColumnType(
                        "enum('Receipt','Issue','Adjustment','Return')"
                    )
                    .IsRequired();

                entity.Property(e => e.Quantity)
                    .HasColumnName("Quantity")
                    .HasPrecision(10, 2);

                entity.Property(e => e.UnitCost)
                    .HasColumnName("UnitCost")
                    .HasPrecision(10, 2);

                entity.Property(e => e.ReferenceNumber)
                    .HasColumnName("ReferenceNumber")
                    .HasMaxLength(50);

                entity.Property(e => e.Notes)
                    .HasColumnName("Notes");

                entity.Property(e => e.TransactionDate)
                    .HasColumnName("TransactionDate");

                entity.Property(e => e.PerformedBy)
                    .HasColumnName("PerformedBy");
            });


            // =================================================
            // PRODUCTION BATCH
            // =================================================

            modelBuilder.Entity<ProductionBatch>(entity =>
            {
                entity.ToTable("production_batch");

                entity.HasKey(e => e.BatchID);

                entity.Property(e => e.BatchID)
                    .HasColumnName("BatchID");

                entity.Property(e => e.BatchNumber)
                    .HasColumnName("BatchNumber")
                    .HasMaxLength(50)
                    .IsRequired();

                entity.HasIndex(e => e.BatchNumber)
                    .IsUnique();

                entity.Property(e => e.ProductID)
                    .HasColumnName("ProductID");

                entity.Property(e => e.QuantityProduced)
                    .HasColumnName("QuantityProduced")
                    .HasPrecision(10, 2);

                entity.Property(e => e.QuantityDefective)
                    .HasColumnName("QuantityDefective")
                    .HasPrecision(10, 2);

                entity.Property(e => e.RawMaterialUsed)
                    .HasColumnName("RawMaterialUsed")
                    .HasColumnType("longtext");

                entity.Property(e => e.ProductionDate)
                    .HasColumnName("ProductionDate");

                entity.Property(e => e.Shift)
                    .HasColumnName("Shift")
                    .HasColumnType(
                        "enum('Morning','Afternoon','Night')"
                    );

                entity.Property(e => e.LineNumber)
                    .HasColumnName("LineNumber")
                    .HasMaxLength(20);

                entity.Property(e => e.SupervisorID)
                    .HasColumnName("SupervisorID");

                entity.Property(e => e.EmployeeID)
                    .HasColumnName("EmployeeID");

                entity.Property(e => e.QualityStatus)
                    .HasColumnName("QualityStatus")
                    .HasColumnType(
                        "enum('Pending','Approved','Rejected')"
                    );

                entity.Property(e => e.QualityCheckedBy)
                    .HasColumnName("QualityCheckedBy");

                entity.Property(e => e.QualityCheckDate)
                    .HasColumnName("QualityCheckDate");

                entity.Property(e => e.QualityNotes)
                    .HasColumnName("QualityNotes");

                entity.Property(e => e.RecordedAt)
                    .HasColumnName("RecordedAt");

                entity.Property(e => e.StartTime)
                    .HasColumnName("StartTime");

                entity.Property(e => e.EndTime)
                    .HasColumnName("EndTime");
            });


            // =================================================
            // LOGIN ATTEMPTS
            // =================================================

            modelBuilder.Entity<LoginAttempt>(entity =>
            {
                entity.ToTable("login_attempts");

                entity.HasKey(e => e.AttemptID);

                entity.Property(e => e.AttemptID)
                    .HasColumnName("AttemptID");

                entity.Property(e => e.EmailAddress)
                    .HasColumnName("EmailAddress")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.IPAddress)
                    .HasColumnName("IPAddress")
                    .HasMaxLength(45)
                    .IsRequired();

                entity.Property(e => e.AttemptTime)
                    .HasColumnName("AttemptTime");

                entity.Property(e => e.Success)
                    .HasColumnName("Success");
            });


            // =================================================
            // SHIFT TEAM
            // =================================================

            modelBuilder.Entity<ShiftTeam>(entity =>
            {
                entity.ToTable("shift_team");

                entity.HasKey(e => e.ShiftTeamID);

                entity.Property(e => e.ShiftTeamID)
                    .HasColumnName("ShiftTeamID");

                entity.Property(e => e.ShiftDate)
                    .HasColumnName("ShiftDate")
                    .HasColumnType("date");

                entity.Property(e => e.Shift)
                    .HasColumnName("Shift")
                    .HasMaxLength(20)
                    .IsRequired();

                entity.Property(e => e.SupervisorID)
                    .HasColumnName("SupervisorID");

                entity.Property(e => e.LineNumber)
                    .HasColumnName("LineNumber")
                    .HasMaxLength(20);

                entity.Property(e => e.TotalEmployees)
                    .HasColumnName("TotalEmployees");

                entity.Property(e => e.Notes)
                    .HasColumnName("Notes");

                entity.Property(e => e.CreatedAt)
                    .HasColumnName("CreatedAt");
            });
        }
    }
}