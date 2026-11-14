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
        // DATABASE TABLES
        // ====================================================

        public DbSet<Employee> Employees { get; set; }

        public DbSet<FinishedGood> FinishedGoods { get; set; }

        public DbSet<RawMaterial> RawMaterials { get; set; }

        public DbSet<InventoryTransaction> InventoryTransactions { get; set; }

        public DbSet<ProductionBatch> ProductionBatches { get; set; }

        public DbSet<MaterialRequest> MaterialRequests { get; set; }

        public DbSet<MaterialRequestItem> MaterialRequestItems { get; set; }

        public DbSet<LoginAttempt> LoginAttempts { get; set; }

        public DbSet<ShiftTeam> ShiftTeams { get; set; }

        public DbSet<SystemActivity> SystemActivities { get; set; }

        public DbSet<PasswordReset> PasswordResets { get; set; }

        public DbSet<CustomerQuotation> CustomerQuotations { get; set; }

        public DbSet<CustomerOrderStatusHistory> CustomerOrderStatusHistories { get; set; }

        public DbSet<EmailNotificationLog> EmailNotificationLogs { get; set; }

        public DbSet<CustomerVerificationToken> CustomerVerificationTokens { get; set; }

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

                // =============================================
                // GOOGLE ACCOUNT LINK
                // =============================================
                //
                // Google's unique account identifier (sub).
                //
                // NULL means the employee has not linked
                // a Google account.
                //
                // The identifier must only be assigned
                // through a secure account-linking process.
                // =============================================

                entity.Property(e => e.GoogleSubjectId)
                    .HasColumnName("GoogleSubjectId")
                    .HasMaxLength(255)
                    .IsRequired(false);

                // A Google account can only be linked
                // to one employee.

                entity.HasIndex(e => e.GoogleSubjectId)
                    .IsUnique()
                    .HasDatabaseName("UX_employee_GoogleSubjectId");

                // =============================================
                // ROLE
                // =============================================
                //
                // Newly registered employees have no role.
                // The administrator assigns the role before
                // approving the employee.
                // =============================================

                entity.Property(e => e.Role)
                    .HasColumnName("Role")
                    .HasColumnType("character varying(40)")
                    .IsRequired(false);

                // =============================================
                // EMPLOYEE STATUS
                // =============================================

                entity.Property(e => e.EmployeeStatus)
                    .HasColumnName("EmployeeStatus")
                    .HasColumnType("character varying(40)");

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
                    .HasColumnType("character varying(40)");

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
                entity.ToTable("inventory_transaction");

                entity.HasKey(e => e.TransactionID);

                entity.Property(e => e.TransactionID)
                    .HasColumnName("TransactionID");

                entity.Property(e => e.MaterialID)
                    .HasColumnName("MaterialID");

                entity.Property(e => e.ProductID)
                    .HasColumnName("ProductID");

                entity.Property(e => e.TransactionType)
                    .HasColumnName("TransactionType")
                    .HasColumnType("character varying(40)")
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

                // Optional: existing standalone batches retain NULL.
                entity.Property(e => e.QuotationID)
                    .HasColumnName("QuotationID")
                    .IsRequired(false);

                entity.HasIndex(e => e.QuotationID)
                    .HasDatabaseName("IX_production_batch_QuotationID");

                entity.HasOne<CustomerQuotation>()
                    .WithMany()
                    .HasForeignKey(e => e.QuotationID)
                    .OnDelete(DeleteBehavior.Restrict);

                entity.Property(e => e.QuantityProduced)
                    .HasColumnName("QuantityProduced")
                    .HasPrecision(10, 2);

                entity.Property(e => e.QuantityDefective)
                    .HasColumnName("QuantityDefective")
                    .HasPrecision(10, 2);

                entity.Property(e => e.RawMaterialUsed)
                    .HasColumnName("RawMaterialUsed")
                    .HasColumnType("text");

                entity.Property(e => e.ProductionDate)
                    .HasColumnName("ProductionDate");

                entity.Property(e => e.Shift)
                    .HasColumnName("Shift")
                    .HasColumnType("character varying(40)");

                entity.Property(e => e.LineNumber)
                    .HasColumnName("LineNumber")
                    .HasMaxLength(20);

                entity.Property(e => e.SupervisorID)
                    .HasColumnName("SupervisorID");

                entity.Property(e => e.EmployeeID)
                    .HasColumnName("EmployeeID");

                entity.Property(e => e.QualityStatus)
                    .HasColumnName("QualityStatus")
                    .HasColumnType("character varying(40)");

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
            // MATERIAL REQUEST
            // =================================================

            modelBuilder.Entity<MaterialRequest>(entity =>
            {
                entity.ToTable("material_request");

                entity.HasKey(e => e.RequestID);

                entity.Property(e => e.RequestID)
                    .HasColumnName("RequestID")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.BatchID)
                    .HasColumnName("BatchID")
                    .IsRequired();

                entity.Property(e => e.RequestedBy)
                    .HasColumnName("RequestedBy")
                    .IsRequired();

                entity.Property(e => e.RequestedAt)
                    .HasColumnName("RequestedAt")
                    .IsRequired();

                entity.Property(e => e.Status)
                    .HasColumnName("Status")
                    .HasMaxLength(30)
                    .HasDefaultValue("Pending")
                    .IsRequired();

                entity.Property(e => e.Notes)
                    .HasColumnName("Notes")
                    .HasColumnType("text");

                entity.HasIndex(e => e.BatchID);
                entity.HasIndex(e => e.RequestedBy);
                entity.HasIndex(e => e.Status);
                entity.HasIndex(e => e.RequestedAt);

                entity.HasOne(e => e.ProductionBatch)
                    .WithMany()
                    .HasForeignKey(e => e.BatchID)
                    .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(e => e.RequestingEmployee)
                    .WithMany()
                    .HasForeignKey(e => e.RequestedBy)
                    .OnDelete(DeleteBehavior.Restrict);
            });

            // =================================================
            // MATERIAL REQUEST ITEM
            // =================================================

            modelBuilder.Entity<MaterialRequestItem>(entity =>
            {
                entity.ToTable("material_request_item");

                entity.HasKey(e => e.RequestItemID);

                entity.Property(e => e.RequestItemID)
                    .HasColumnName("RequestItemID")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.RequestID)
                    .HasColumnName("RequestID")
                    .IsRequired();

                entity.Property(e => e.MaterialID)
                    .HasColumnName("MaterialID")
                    .IsRequired();

                entity.Property(e => e.QuantityRequested)
                    .HasColumnName("QuantityRequested")
                    .HasPrecision(10, 2)
                    .IsRequired();

                entity.Property(e => e.QuantityIssued)
                    .HasColumnName("QuantityIssued")
                    .HasPrecision(10, 2)
                    .HasDefaultValue(0m)
                    .IsRequired();

                entity.Property(e => e.Notes)
                    .HasColumnName("Notes")
                    .HasColumnType("text");

                entity.HasIndex(e => e.RequestID);
                entity.HasIndex(e => e.MaterialID);
                entity.HasIndex(e => new { e.RequestID, e.MaterialID }).IsUnique();

                entity.HasOne(e => e.MaterialRequest)
                    .WithMany(e => e.Items)
                    .HasForeignKey(e => e.RequestID)
                    .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne(e => e.RawMaterial)
                    .WithMany()
                    .HasForeignKey(e => e.MaterialID)
                    .OnDelete(DeleteBehavior.Restrict);
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

            // =================================================
            // SYSTEM ACTIVITY
            // =================================================

            modelBuilder.Entity<SystemActivity>(entity =>
            {
                entity.ToTable("system_activity");

                entity.HasKey(e => e.ActivityID);

                entity.Property(e => e.ActivityID)
                    .HasColumnName("ActivityID");

                entity.Property(e => e.EmployeeID)
                    .HasColumnName("EmployeeID");

                entity.Property(e => e.ActionType)
                    .HasColumnName("ActionType")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.Description)
                    .HasColumnName("Description")
                    .HasMaxLength(500)
                    .IsRequired();

                entity.Property(e => e.EntityType)
                    .HasColumnName("EntityType")
                    .HasMaxLength(100);

                entity.Property(e => e.EntityID)
                    .HasColumnName("EntityID");

                entity.Property(e => e.ActivityTime)
                    .HasColumnName("ActivityTime");

                entity.Property(e => e.IPAddress)
                    .HasColumnName("IPAddress")
                    .HasMaxLength(45);

                entity.HasOne(e => e.Employee)
                    .WithMany()
                    .HasForeignKey(e => e.EmployeeID)
                    .OnDelete(DeleteBehavior.SetNull);
            });

            // =================================================
            // PASSWORD RESET
            // =================================================

            modelBuilder.Entity<PasswordReset>(entity =>
            {
                entity.ToTable("password_reset");

                entity.HasKey(e => e.PasswordResetID);

                entity.Property(e => e.PasswordResetID)
                    .HasColumnName("PasswordResetID");

                entity.Property(e => e.EmployeeID)
                    .HasColumnName("EmployeeID")
                    .IsRequired();

                entity.Property(e => e.TokenHash)
                    .HasColumnName("TokenHash")
                    .HasMaxLength(255)
                    .IsRequired();

                entity.Property(e => e.ExpiresAt)
                    .HasColumnName("ExpiresAt")
                    .IsRequired();

                entity.Property(e => e.UsedAt)
                    .HasColumnName("UsedAt");

                entity.Property(e => e.CreatedAt)
                    .HasColumnName("CreatedAt")
                    .IsRequired();

                // =============================================
                // EMPLOYEE FOREIGN KEY
                // =============================================

                entity.HasOne(e => e.Employee)
                    .WithMany()
                    .HasForeignKey(e => e.EmployeeID)
                    .OnDelete(DeleteBehavior.Cascade);
            });

            // =================================================
            // CUSTOMER QUOTATION
            // =================================================

            modelBuilder.Entity<CustomerQuotation>(entity =>
            {
                entity.ToTable("customer_quotation");

                entity.HasKey(e => e.QuotationID);

                entity.Property(e => e.QuotationID)
                    .HasColumnName("QuotationID")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.ReferenceNumber)
                    .HasColumnName("ReferenceNumber")
                    .HasMaxLength(50)
                    .IsRequired();

                entity.HasIndex(e => e.ReferenceNumber)
                    .IsUnique();

                entity.Property(e => e.FullName)
                    .HasColumnName("FullName")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.Company)
                    .HasColumnName("Company")
                    .HasMaxLength(150);

                entity.Property(e => e.Email)
                    .HasColumnName("Email")
                    .HasMaxLength(254)
                    .IsRequired();

                entity.Property(e => e.Phone)
                    .HasColumnName("Phone")
                    .HasMaxLength(30)
                    .IsRequired();

                entity.Property(e => e.Category)
                    .HasColumnName("Category")
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.Quantity)
                    .HasColumnName("Quantity")
                    .IsRequired();

                entity.Property(e => e.Deadline)
                    .HasColumnName("Deadline")
                    .IsRequired();

                entity.Property(e => e.Specifications)
                    .HasColumnName("Specifications")
                    .HasColumnType("text")
                    .IsRequired();

                entity.Property(e => e.Status)
                    .HasColumnName("Status")
                    .HasMaxLength(30)
                    .HasDefaultValue("Pending")
                    .IsRequired();

                entity.Property(e => e.CreatedAtUtc)
                    .HasColumnName("CreatedAtUtc")
                    .IsRequired();

                entity.Property(e => e.ReviewedByEmployeeID)
                    .HasColumnName("ReviewedByEmployeeID");

                entity.Property(e => e.ReviewedAtUtc)
                    .HasColumnName("ReviewedAtUtc");

                entity.Property(e => e.ManagerNotes)
                    .HasColumnName("ManagerNotes")
                    .HasColumnType("text");

                entity.Property(e => e.QuotedAmount)
                    .HasColumnName("QuotedAmount")
                    .HasPrecision(18, 2);

                entity.Property(e => e.CustomerDecision)
                    .HasColumnName("CustomerDecision")
                    .HasMaxLength(20)
                    .HasDefaultValue("Pending")
                    .IsRequired();

                entity.Property(e => e.CustomerDecisionAtUtc)
                    .HasColumnName("CustomerDecisionAtUtc");

                entity.HasIndex(e => e.CustomerDecision);
                entity.HasIndex(e => e.Status);
                entity.HasIndex(e => e.CreatedAtUtc);
                entity.HasIndex(e => e.ReviewedByEmployeeID);

                entity.HasOne<Employee>()
                    .WithMany()
                    .HasForeignKey(e => e.ReviewedByEmployeeID)
                    .OnDelete(DeleteBehavior.SetNull);
            });

            // =================================================
            // CUSTOMER ORDER STATUS HISTORY
            // =================================================

            modelBuilder.Entity<CustomerOrderStatusHistory>(entity =>
            {
                entity.ToTable("customer_order_status_history");

                entity.HasKey(e => e.OrderStatusHistoryID);
                entity.Property(e => e.OrderStatusHistoryID)
                    .HasColumnName("OrderStatusHistoryID")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.QuotationID).HasColumnName("QuotationID").IsRequired();
                entity.Property(e => e.BatchID).HasColumnName("BatchID");
                entity.Property(e => e.Status).HasColumnName("Status").HasMaxLength(50).IsRequired();
                entity.Property(e => e.Description).HasColumnName("Description").HasColumnType("text");
                entity.Property(e => e.CreatedAtUtc).HasColumnName("CreatedAtUtc").IsRequired();
                entity.Property(e => e.UpdatedByEmployeeID).HasColumnName("UpdatedByEmployeeID");
                entity.Property(e => e.IsVisibleToCustomer)
                    .HasColumnName("IsVisibleToCustomer")
                    .HasDefaultValue(true)
                    .IsRequired();

                entity.HasIndex(e => new { e.QuotationID, e.CreatedAtUtc })
                    .HasDatabaseName("IX_order_history_QuotationID_CreatedAtUtc");
                entity.HasIndex(e => e.BatchID);
                entity.HasIndex(e => e.UpdatedByEmployeeID);

                entity.HasOne<CustomerQuotation>()
                    .WithMany()
                    .HasForeignKey(e => e.QuotationID)
                    .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<ProductionBatch>()
                    .WithMany()
                    .HasForeignKey(e => e.BatchID)
                    .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<Employee>()
                    .WithMany()
                    .HasForeignKey(e => e.UpdatedByEmployeeID)
                    .OnDelete(DeleteBehavior.SetNull);
            });

            // =================================================
            // EMAIL NOTIFICATION LOG
            // =================================================

            modelBuilder.Entity<EmailNotificationLog>(entity =>
            {
                entity.ToTable("email_notification_log");

                entity.HasKey(e => e.EmailNotificationLogID);
                entity.Property(e => e.EmailNotificationLogID)
                    .HasColumnName("EmailNotificationLogID")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.QuotationID).HasColumnName("QuotationID").IsRequired();
                entity.Property(e => e.BatchID).HasColumnName("BatchID");
                entity.Property(e => e.RecipientEmail)
                    .HasColumnName("RecipientEmail").HasMaxLength(254).IsRequired();
                entity.Property(e => e.Subject)
                    .HasColumnName("Subject").HasMaxLength(255).IsRequired();
                entity.Property(e => e.Body)
                    .HasColumnName("Body").HasColumnType("text").IsRequired();
                entity.Property(e => e.NotificationType)
                    .HasColumnName("NotificationType").HasMaxLength(60).IsRequired();
                entity.Property(e => e.Status)
                    .HasColumnName("Status").HasMaxLength(20)
                    .HasDefaultValue("Pending").IsRequired();
                entity.Property(e => e.DeduplicationKey)
                    .HasColumnName("DeduplicationKey").HasMaxLength(191).IsRequired();
                entity.Property(e => e.RetryCount)
                    .HasColumnName("RetryCount").HasDefaultValue(0).IsRequired();
                entity.Property(e => e.MaxRetries)
                    .HasColumnName("MaxRetries").HasDefaultValue(5).IsRequired();
                entity.Property(e => e.CreatedAtUtc).HasColumnName("CreatedAtUtc").IsRequired();
                entity.Property(e => e.LastAttemptAtUtc).HasColumnName("LastAttemptAtUtc");
                entity.Property(e => e.NextAttemptAtUtc).HasColumnName("NextAttemptAtUtc");
                entity.Property(e => e.SentAtUtc).HasColumnName("SentAtUtc");
                entity.Property(e => e.LastError).HasColumnName("LastError").HasColumnType("text");

                entity.HasIndex(e => e.DeduplicationKey)
                    .IsUnique()
                    .HasDatabaseName("UX_email_notification_log_DeduplicationKey");
                entity.HasIndex(e => new { e.Status, e.NextAttemptAtUtc });
                entity.HasIndex(e => e.QuotationID);
                entity.HasIndex(e => e.BatchID);

                entity.HasOne<CustomerQuotation>()
                    .WithMany()
                    .HasForeignKey(e => e.QuotationID)
                    .OnDelete(DeleteBehavior.Restrict);

                entity.HasOne<ProductionBatch>()
                    .WithMany()
                    .HasForeignKey(e => e.BatchID)
                    .OnDelete(DeleteBehavior.Restrict);
            });

            // =================================================
            // CUSTOMER VERIFICATION TOKEN
            // =================================================

            modelBuilder.Entity<CustomerVerificationToken>(entity =>
            {
                entity.ToTable("customer_verification_token");

                entity.HasKey(e => e.CustomerVerificationTokenID);
                entity.Property(e => e.CustomerVerificationTokenID)
                    .HasColumnName("CustomerVerificationTokenID")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.QuotationID).HasColumnName("QuotationID").IsRequired();
                entity.Property(e => e.TokenHash)
                    .HasColumnName("TokenHash").HasMaxLength(64).IsRequired();
                entity.Property(e => e.Purpose)
                    .HasColumnName("Purpose").HasMaxLength(50).IsRequired();
                entity.Property(e => e.CreatedAtUtc).HasColumnName("CreatedAtUtc").IsRequired();
                entity.Property(e => e.ExpiresAtUtc).HasColumnName("ExpiresAtUtc").IsRequired();
                entity.Property(e => e.UsedAtUtc).HasColumnName("UsedAtUtc");
                entity.Property(e => e.RevokedAtUtc).HasColumnName("RevokedAtUtc");

                entity.HasIndex(e => e.TokenHash)
                    .IsUnique()
                    .HasDatabaseName("UX_customer_verification_token_TokenHash");
                entity.HasIndex(e => new { e.QuotationID, e.Purpose });
                entity.HasIndex(e => e.ExpiresAtUtc);

                entity.HasOne<CustomerQuotation>()
                    .WithMany()
                    .HasForeignKey(e => e.QuotationID)
                    .OnDelete(DeleteBehavior.Restrict);
            });

            // PostgreSQL schema uses timestamp WITHOUT time zone for
            // all DateTime/DateTime? columns. DateOnly and TimeOnly
            // properties retain their native date/time mappings.
            foreach (var entityType in modelBuilder.Model.GetEntityTypes())
            {
                foreach (var property in entityType.GetProperties())
                {
                    if (property.ClrType == typeof(DateTime) ||
                        property.ClrType == typeof(DateTime?))
                    {
                        property.SetColumnType("timestamp without time zone");
                    }
                }
            }
        }
    }
}