<?php
require_once "../includes/auth.php";
require_role('ProductionManager');
require_once "../connect_db.php";

function generate_batch_number() {
    return 'BATCH-' . date('Ymd') . '-' . strtoupper(substr(uniqid(), -4));
}

// Get products - only approved ones
$products = $conn->query("SELECT ProductID, ProductName, Unit FROM finished_good WHERE QualityStatus = 'Approved' ORDER BY ProductName");

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    validate_csrf();
    
    $batch_number = generate_batch_number();
    $product_id = intval($_POST['product_id']);
    $quantity = floatval($_POST['quantity']);
    $defective = floatval($_POST['defective'] ?? 0);
    $shift = $_POST['shift'];
    $notes = trim($_POST['notes'] ?? '');
    $supervisor_id = $_SESSION['employee_id'];
    
    $sql = "INSERT INTO production_batch (BatchNumber, ProductID, QuantityProduced, QuantityDefective, Shift, SupervisorID, QualityNotes, ProductionDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        $_SESSION['error'] = "Prepare failed: " . $conn->error;
        header("Location: record_output.php");
        exit();
    }
    
    // FIX: Correct type string: s, i, d, d, s, i, s = "siddsis"
    $stmt->bind_param("siddsis", $batch_number, $product_id, $quantity, $defective, $shift, $supervisor_id, $notes);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Production batch recorded. Batch: $batch_number";
    } else {
        $_SESSION['error'] = "Failed to record production: " . $stmt->error;
    }
    $stmt->close();
    header("Location: dashboard.php");
    exit();
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Record Production Output</h1>
    <p>Log daily production results and track output</p>
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
    <form method="POST" action="">
        <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
        
        <div class="form-group">
            <label>Product <span class="required">*</span></label>
            <select name="product_id" class="form-control" required>
                <option value="">Select Product</option>
                <?php while ($product = $products->fetch_assoc()): ?>
                <option value="<?php echo $product['ProductID']; ?>"><?php echo h($product['ProductName']); ?> (<?php echo h($product['Unit']); ?>)</option>
                <?php endwhile; ?>
            </select>
            <?php if ($products->num_rows == 0): ?>
            <small style="color: #ed6c02;">No approved products available. Please check finished goods.</small>
            <?php endif; ?>
        </div>
        
        <div class="form-row two-columns">
            <div class="form-group">
                <label>Quantity Produced <span class="required">*</span></label>
                <input type="number" name="quantity" step="1" class="form-control" required min="1">
            </div>
            <div class="form-group">
                <label>Defective Quantity</label>
                <input type="number" name="defective" step="1" class="form-control" value="0" min="0">
                <small>Must be less than or equal to quantity produced</small>
            </div>
        </div>
        
        <div class="form-group">
            <label>Shift <span class="required">*</span></label>
            <select name="shift" class="form-control" required>
                <option value="">Select Shift</option>
                <option value="Morning">Morning Shift (06:00 - 14:00)</option>
                <option value="Afternoon">Afternoon Shift (14:00 - 22:00)</option>
                <option value="Night">Night Shift (22:00 - 06:00)</option>
            </select>
        </div>
        
        <div class="form-group">
            <label>Production Notes</label>
            <textarea name="notes" class="form-control" rows="3" placeholder="Any issues, observations, or remarks..."></textarea>
        </div>
        
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <span class="material-icons">save</span>
                Record Production
            </button>
            <a href="dashboard.php" class="btn btn-secondary">
                <span class="material-icons">cancel</span>
                Cancel
            </a>
        </div>
    </form>
</div>

<script>
// Validate that defective quantity doesn't exceed produced quantity
document.querySelector('form').addEventListener('submit', function(e) {
    const quantity = parseInt(document.querySelector('input[name="quantity"]').value);
    const defective = parseInt(document.querySelector('input[name="defective"]').value);
    
    if (defective > quantity) {
        e.preventDefault();
        alert('Defective quantity cannot exceed quantity produced.');
        return false;
    }
    return true;
});
</script>

<style>
.form-container {
    max-width: 600px;
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
    margin-bottom: 20px;
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
</style>

<?php include "../includes/footer.php"; ?>