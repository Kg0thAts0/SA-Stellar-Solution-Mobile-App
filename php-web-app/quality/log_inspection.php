<?php
require_once "../includes/auth.php";
require_role('QualityController');
require_once "../connect_db.php";

// Get pending batches
$batches = $conn->query("SELECT b.BatchID, b.BatchNumber, p.ProductName, b.QuantityProduced 
                         FROM production_batch b LEFT JOIN finished_good p ON b.ProductID = p.ProductID 
                         WHERE b.QualityStatus IN ('Pending', 'Conditional') ORDER BY b.ProductionDate ASC");

// Get count of pending batches for the badge
$pending_count = $batches->num_rows;

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    validate_csrf();
    
    $batch_id = intval($_POST['batch_id']);
    $result = $_POST['result'];
    $defect_type = trim($_POST['defect_type'] ?? '');
    $defect_qty = floatval($_POST['defect_quantity'] ?? 0);
    $remarks = trim($_POST['remarks'] ?? '');
    $inspector_id = $_SESSION['employee_id'];
    
    // Validate: If result is Fail, defect type and quantity should be provided
    if ($result === 'Fail' && empty($defect_type)) {
        $_SESSION['error'] = "Defect type is required when result is Fail.";
        header("Location: log_inspection.php");
        exit();
    }
    
    if ($result === 'Fail' && $defect_qty <= 0) {
        $_SESSION['error'] = "Defect quantity must be greater than 0 when result is Fail.";
        header("Location: log_inspection.php");
        exit();
    }
    
    $conn->begin_transaction();
    
    try {
        // CORRECT: "iissds" = i, i, s, s, d, s
        $stmt = $conn->prepare("INSERT INTO quality_inspection (BatchID, InspectorID, Result, DefectType, DefectQuantity, Remarks) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("iissds", $batch_id, $inspector_id, $result, $defect_type, $defect_qty, $remarks);
        
        if (!$stmt->execute()) {
            throw new Exception($stmt->error);
        }
        $stmt->close();
        
        $new_status = $result === 'Pass' ? 'Approved' : ($result === 'Fail' ? 'Rejected' : 'Conditional');
        
        // CORRECT: "sii" = s, i, i
        $stmt2 = $conn->prepare("UPDATE production_batch SET QualityStatus = ?, QualityCheckedBy = ?, QualityCheckDate = NOW() WHERE BatchID = ?");
        $stmt2->bind_param("sii", $new_status, $inspector_id, $batch_id);
        
        if (!$stmt2->execute()) {
            throw new Exception($stmt2->error);
        }
        $stmt2->close();
        
        $conn->commit();
        $_SESSION['success'] = "Inspection recorded successfully. Batch marked as: $new_status";
        
    } catch (Exception $e) {
        $conn->rollback();
        $_SESSION['error'] = "Failed to record inspection: " . $e->getMessage();
        error_log("QC Inspection error: " . $e->getMessage());
    }
    
    header("Location: dashboard.php");
    exit();
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Log Quality Inspection</h1>
    <p>Record quality check results for production batches</p>
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

<div class="form-container">
    <form method="POST" action="" id="inspectionForm">
        <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
        
        <div class="form-group">
            <label>Select Batch <span class="required">*</span></label>
            <select name="batch_id" class="form-control" required>
                <option value="">Select a batch to inspect</option>
                <?php while ($batch = $batches->fetch_assoc()): ?>
                <option value="<?php echo $batch['BatchID']; ?>">
                    <?php echo h($batch['BatchNumber']); ?> - <?php echo h($batch['ProductName'] ?? 'N/A'); ?> (<?php echo number_format($batch['QuantityProduced']); ?> units)
                </option>
                <?php endwhile; ?>
            </select>
            <?php if ($pending_count == 0): ?>
            <small style="color: #2e7d32;">No pending batches. All batches have been inspected.</small>
            <?php else: ?>
            <small style="color: #ed6c02;"><?php echo $pending_count; ?> batch(es) pending inspection</small>
            <?php endif; ?>
        </div>
        
        <div class="form-group">
            <label>Inspection Result <span class="required">*</span></label>
            <div class="radio-group">
                <label>
                    <input type="radio" name="result" value="Pass" required onchange="toggleDefectFields()">
                    <span class="badge badge-pass">Pass</span>
                </label>
                <label>
                    <input type="radio" name="result" value="Fail" onchange="toggleDefectFields()">
                    <span class="badge badge-fail">Fail</span>
                </label>
                <label>
                    <input type="radio" name="result" value="Conditional" onchange="toggleDefectFields()">
                    <span class="badge badge-conditional">Conditional</span>
                </label>
            </div>
        </div>
        
        <div class="form-row two-columns" id="defectFields" style="display: none;">
            <div class="form-group">
                <label>Defect Type <span class="required" id="defectRequired" style="display: none;">*</span></label>
                <select name="defect_type" class="form-control">
                    <option value="">None</option>
                    <option value="Fabric Defect">Fabric Defect</option>
                    <option value="Stitching Issue">Stitching Issue</option>
                    <option value="Size Mismatch">Size Mismatch</option>
                    <option value="Color Variation">Color Variation</option>
                    <option value="Packaging Error">Packaging Error</option>
                    <option value="Other">Other</option>
                </select>
            </div>
            <div class="form-group">
                <label>Defect Quantity</label>
                <input type="number" name="defect_quantity" step="1" class="form-control" min="0" value="0">
                <small>Number of units affected</small>
            </div>
        </div>
        
        <div class="form-group">
            <label>Remarks / Notes</label>
            <textarea name="remarks" class="form-control" rows="3" placeholder="Additional observations or notes..."></textarea>
        </div>
        
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <span class="material-icons">fact_check</span>
                Submit Inspection
            </button>
            <a href="dashboard.php" class="btn btn-secondary">
                <span class="material-icons">cancel</span>
                Cancel
            </a>
        </div>
    </form>
</div>

<?php if ($pending_count == 0): ?>
<div class="alert alert-info mt-2">
    <span class="material-icons">info</span>
    <span>No pending batches available for inspection. All batches have been reviewed.</span>
</div>
<?php endif; ?>

<script>
function toggleDefectFields() {
    const result = document.querySelector('input[name="result"]:checked');
    const defectFields = document.getElementById('defectFields');
    const defectRequired = document.getElementById('defectRequired');
    
    if (result && result.value === 'Fail') {
        defectFields.style.display = 'grid';
        defectRequired.style.display = 'inline';
        document.querySelector('select[name="defect_type"]').required = true;
        document.querySelector('input[name="defect_quantity"]').required = true;
    } else {
        defectFields.style.display = 'none';
        defectRequired.style.display = 'none';
        document.querySelector('select[name="defect_type"]').required = false;
        document.querySelector('input[name="defect_quantity"]').required = false;
    }
}
</script>

<style>
.form-container {
    max-width: 650px;
    margin: 0 auto;
    background: white;
    border-radius: 16px;
    padding: 32px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.form-group {
    margin-bottom: 20px;
}
.form-group label {
    display: block;
    font-size: 13px;
    font-weight: 500;
    margin-bottom: 6px;
}
.form-group select, .form-group input, .form-group textarea {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;
    font-family: inherit;
}
.form-group select:focus, .form-group input:focus, .form-group textarea:focus {
    outline: none;
    border-color: #c9a03d;
}
.form-group small {
    display: block;
    margin-top: 4px;
    font-size: 11px;
    color: #6c757d;
}
.form-row.two-columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}
.required {
    color: #c62828;
}
.radio-group {
    display: flex;
    gap: 24px;
    margin-top: 8px;
}
.radio-group label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: normal;
    cursor: pointer;
}
.radio-group input[type="radio"] {
    width: auto;
    padding: 0;
}
.badge {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}
.badge-pass {
    background: #e8f5e9;
    color: #2e7d32;
}
.badge-fail {
    background: #ffebee;
    color: #c62828;
}
.badge-conditional {
    background: #e3f2fd;
    color: #0288d1;
}
.form-actions {
    display: flex;
    gap: 12px;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid #e0e0e0;
}
.btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 24px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    border: none;
    text-decoration: none;
    transition: all 0.3s ease;
}
.btn-primary {
    background: #1a2634;
    color: white;
}
.btn-primary:hover {
    background: #2c3e4e;
}
.btn-secondary {
    background: #e0e0e0;
    color: #2c3e50;
}
.btn-secondary:hover {
    background: #d0d0d0;
}
.alert {
    padding: 12px 16px;
    border-radius: 8px;
    margin: 20px 0;
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
.alert-info {
    background: #e3f2fd;
    color: #0288d1;
    border-left: 4px solid #0288d1;
}
.mt-2 {
    margin-top: 20px;
}
</style>

<?php include "../includes/footer.php"; ?>