<?php
require_once "../includes/auth.php";
require_role('QualityController');
require_once "../connect_db.php";

// Get pending and conditional batches
$pending = $conn->query("SELECT b.BatchID, b.BatchNumber, p.ProductName, b.QuantityProduced, b.QualityStatus, b.ProductionDate 
                         FROM production_batch b LEFT JOIN finished_good p ON b.ProductID = p.ProductID 
                         WHERE b.QualityStatus IN ('Pending', 'Conditional') ORDER BY b.ProductionDate ASC");

$pending_count = $pending->num_rows;

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    validate_csrf();
    
    $batch_id = intval($_POST['batch_id']);
    $action = $_POST['action'];
    $notes = trim($_POST['notes'] ?? '');
    $inspector_id = $_SESSION['employee_id'];
    $new_status = $action === 'approve' ? 'Approved' : 'Rejected';
    
    // If rejecting, ensure there's a reason
    if ($action === 'reject' && empty($notes)) {
        $_SESSION['error'] = "Rejection reason is required.";
        header("Location: qc_batches.php");
        exit();
    }
    
    // CORRECT: "sisi" = s, i, s, i
    $stmt = $conn->prepare("UPDATE production_batch SET QualityStatus = ?, QualityCheckedBy = ?, QualityCheckDate = NOW(), QualityNotes = ? WHERE BatchID = ?");
    $stmt->bind_param("sisi", $new_status, $inspector_id, $notes, $batch_id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Batch {$new_status} successfully.";
    } else {
        $_SESSION['error'] = "Failed to update batch: " . $stmt->error;
    }
    $stmt->close();
    header("Location: qc_batches.php");
    exit();
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Batch Approval Dashboard</h1>
    <p>Review and approve/reject production batches</p>
</div>

<!-- Display Error/Success Messages -->
<?php if (isset($_SESSION['error'])): ?>
<div class="alert alert-error">
    <span class="material-icons">error</span>
    <span><?php echo h($_SESSION['error']); ?></span>
</div>
<?php unset($_SESSION['error']); endif; ?>

<?php if (isset($_SESSION['success'])): ?>
<div class="alert alert-success">
    <span class="material-icons">check_circle</span>
    <span><?php echo h($_SESSION['success']); ?></span>
</div>
<?php unset($_SESSION['success']); endif; ?>

<?php if ($pending_count > 0): ?>
<div class="pending-summary">
    <span class="material-icons">info</span>
    <span><strong><?php echo $pending_count; ?></strong> batch(es) awaiting your review</span>
</div>
<?php endif; ?>

<?php while ($batch = $pending->fetch_assoc()): ?>
<div class="batch-card">
    <div class="batch-header">
        <div>
            <h3><?php echo h($batch['BatchNumber']); ?></h3>
            <p><?php echo h($batch['ProductName'] ?? 'N/A'); ?> | <?php echo number_format($batch['QuantityProduced']); ?> units</p>
        </div>
        <span class="badge badge-<?php echo strtolower($batch['QualityStatus']); ?>">
            <?php echo $batch['QualityStatus']; ?>
        </span>
    </div>
    <div class="batch-body">
        <div class="batch-info">
            <span class="material-icons">calendar_today</span>
            Production Date: <?php echo date('d M Y', strtotime($batch['ProductionDate'])); ?>
        </div>
        <div class="batch-actions">
            <!-- Approve Form -->
            <form method="POST" class="batch-form approve-form">
                <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
                <input type="hidden" name="batch_id" value="<?php echo $batch['BatchID']; ?>">
                <input type="hidden" name="action" value="approve">
                <div class="form-group">
                    <textarea name="notes" placeholder="Approval notes (optional)" rows="2" class="form-control-sm"></textarea>
                </div>
                <button type="submit" class="btn btn-success">
                    <span class="material-icons">check_circle</span>
                    Approve
                </button>
            </form>
            
            <!-- Reject Form -->
            <form method="POST" class="batch-form reject-form" onsubmit="return validateReject(this)">
                <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
                <input type="hidden" name="batch_id" value="<?php echo $batch['BatchID']; ?>">
                <input type="hidden" name="action" value="reject">
                <div class="form-group">
                    <textarea name="notes" placeholder="Rejection reason *" rows="2" class="form-control-sm" required></textarea>
                </div>
                <button type="submit" class="btn btn-danger">
                    <span class="material-icons">cancel</span>
                    Reject
                </button>
            </form>
        </div>
    </div>
</div>
<?php endwhile; ?>

<?php if ($pending_count == 0): ?>
<div class="alert alert-success">
    <span class="material-icons">check_circle</span>
    <span>No pending batches. All batches have been reviewed.</span>
</div>
<?php endif; ?>

<style>
.batch-card {
    background: white;
    border-radius: 12px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    transition: all 0.3s ease;
}
.batch-card:hover {
    box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}
.batch-header {
    padding: 20px 24px;
    background: #f8f9fa;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e0e0e0;
}
.batch-header h3 {
    margin: 0 0 5px;
    font-size: 18px;
}
.batch-header p {
    margin: 0;
    color: #6c757d;
    font-size: 14px;
}
.batch-body {
    padding: 20px 24px;
}
.batch-info {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 8px;
    color: #6c757d;
    font-size: 14px;
}
.batch-info .material-icons {
    font-size: 18px;
    color: #6c757d;
}
.batch-actions {
    display: flex;
    gap: 24px;
    flex-wrap: wrap;
}
.batch-form {
    flex: 1;
    min-width: 200px;
}
.batch-form .form-group {
    margin-bottom: 10px;
}
.form-control-sm {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 13px;
    font-family: inherit;
    resize: vertical;
    min-height: 50px;
}
.form-control-sm:focus {
    outline: none;
    border-color: #c9a03d;
}
.btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 8px 20px;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    border: none;
    text-decoration: none;
    transition: all 0.3s ease;
}
.btn-success {
    background: #2e7d32;
    color: white;
}
.btn-success:hover {
    background: #1b5e20;
}
.btn-danger {
    background: #c62828;
    color: white;
}
.btn-danger:hover {
    background: #b71c1c;
}
.badge {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}
.badge-pending {
    background: #fff4e5;
    color: #ed6c02;
}
.badge-conditional {
    background: #e3f2fd;
    color: #0288d1;
}
.badge-approved {
    background: #e8f5e9;
    color: #2e7d32;
}
.badge-rejected {
    background: #ffebee;
    color: #c62828;
}
.alert {
    padding: 12px 16px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 10px;
}
.alert-success {
    background: #e8f5e9;
    color: #2e7d32;
    border-left: 4px solid #2e7d32;
}
.alert-error {
    background: #ffebee;
    color: #c62828;
    border-left: 4px solid #c62828;
}
.pending-summary {
    background: #fff4e5;
    padding: 12px 16px;
    border-radius: 8px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
    color: #ed6c02;
    border-left: 4px solid #ed6c02;
}
</style>

<?php include "../includes/footer.php"; ?>