<?php
require_once "../includes/auth.php";
require_role('InventoryClerk');
require_once "../connect_db.php";

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Helper functions
function format_number($num) {
    return number_format($num ?? 0);
}

function h($str) {
    return htmlspecialchars($str ?? '', ENT_QUOTES, 'UTF-8');
}

function generate_material_code() {
    global $conn;
    $prefix = 'RM';
    $sql = "SELECT MaterialID FROM raw_material ORDER BY MaterialID DESC LIMIT 1";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        $last = $result->fetch_assoc();
        $number = $last['MaterialID'] + 1;
    } else {
        $number = 1;
    }
    return $prefix . str_pad($number, 5, '0', STR_PAD_LEFT);
}

// Handle Add operation
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['action']) && $_POST['action'] === 'add') {
    
    // Validate CSRF
    if (!isset($_POST['csrf_token']) || $_POST['csrf_token'] !== $_SESSION['csrf_token']) {
        $_SESSION['error'] = "CSRF validation failed. Please try again.";
        header("Location: raw_materials.php");
        exit();
    }
    
    $material_code = generate_material_code();
    $material_name = trim($_POST['material_name'] ?? '');
    $category = trim($_POST['category'] ?? '');
    $unit = $_POST['unit'] ?? 'Pieces';
    $current_stock = floatval($_POST['current_stock'] ?? 0);
    $minimum_stock = floatval($_POST['minimum_stock'] ?? 0);
    $reorder_level = floatval($_POST['reorder_level'] ?? 0);
    $unit_cost = floatval($_POST['unit_cost'] ?? 0);
    $supplier_info = trim($_POST['supplier_info'] ?? '');
    
    // Validate required fields
    if (empty($material_name)) {
        $_SESSION['error'] = "Material name is required.";
        header("Location: raw_materials.php");
        exit();
    }
    
    if (empty($unit)) {
        $_SESSION['error'] = "Unit is required.";
        header("Location: raw_materials.php");
        exit();
    }
    
    // CORRECT INSERT - NO SPACES in the type string
    $sql = "INSERT INTO raw_material (MaterialCode, MaterialName, Category, Unit, CurrentStock, MinimumStock, ReorderLevel, UnitCost, SupplierInfo) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        $_SESSION['error'] = "Prepare failed: " . $conn->error;
        header("Location: raw_materials.php");
        exit();
    }
    
    // CRITICAL FIX: No spaces in the type string!
    // 9 parameters: 4 strings (s), 4 doubles (d), 1 string (s)
    // Correct: "ssssdddds" (no spaces)
    $stmt->bind_param("ssssdddds", $material_code, $material_name, $category, $unit, $current_stock, $minimum_stock, $reorder_level, $unit_cost, $supplier_info);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Raw material added successfully. Code: $material_code";
    } else {
        $_SESSION['error'] = "Failed to add: " . $stmt->error;
    }
    $stmt->close();
    
    header("Location: raw_materials.php");
    exit();
}

// Handle Edit operation
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['action']) && $_POST['action'] === 'edit') {
    validate_csrf();
    
    $material_id = intval($_POST['material_id']);
    $material_name = trim($_POST['material_name']);
    $category = trim($_POST['category']);
    $unit = $_POST['unit'];
    $current_stock = floatval($_POST['current_stock']);
    $minimum_stock = floatval($_POST['minimum_stock']);
    $reorder_level = floatval($_POST['reorder_level']);
    $unit_cost = floatval($_POST['unit_cost']);
    $supplier_info = trim($_POST['supplier_info']);
    
    $sql = "UPDATE raw_material SET MaterialName=?, Category=?, Unit=?, CurrentStock=?, MinimumStock=?, ReorderLevel=?, UnitCost=?, SupplierInfo=? WHERE MaterialID=?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssdddd si", $material_name, $category, $unit, $current_stock, $minimum_stock, $reorder_level, $unit_cost, $supplier_info, $material_id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Raw material updated successfully.";
    } else {
        $_SESSION['error'] = "Failed to update raw material: " . $stmt->error;
    }
    $stmt->close();
    header("Location: raw_materials.php");
    exit();
}

// Handle Delete operation
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['action']) && $_POST['action'] === 'delete') {
    validate_csrf();
    
    $material_id = intval($_POST['material_id']);
    $stmt = $conn->prepare("DELETE FROM raw_material WHERE MaterialID = ?");
    $stmt->bind_param("i", $material_id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Raw material deleted successfully.";
    } else {
        $_SESSION['error'] = "Failed to delete raw material: " . $stmt->error;
    }
    $stmt->close();
    header("Location: raw_materials.php");
    exit();
}

// Fetch all raw materials
$materials = $conn->query("SELECT * FROM raw_material ORDER BY MaterialName ASC");
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Raw Materials Management</h1>
    <p>Manage raw material inventory, stock levels, and supplier information</p>
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

<div class="action-bar">
    <button class="btn btn-primary" onclick="openModal('addModal')">
        <span class="material-icons">add</span>
        Add New Material
    </button>
</div>

<div class="card mt-2">
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Material Name</th>
                    <th>Category</th>
                    <th>Current Stock</th>
                    <th>Min Stock</th>
                    <th>Reorder Level</th>
                    <th>Unit Cost</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if ($materials && $materials->num_rows > 0): ?>
                    <?php while ($material = $materials->fetch_assoc()): ?>
                    <tr>
                        <td><?php echo h($material['MaterialCode']); ?></td>
                        <td><?php echo h($material['MaterialName']); ?></td>
                        <td><?php echo h($material['Category'] ?: '-'); ?></td>
                        <td><?php echo format_number($material['CurrentStock']); ?> <?php echo h($material['Unit']); ?></td>
                        <td><?php echo format_number($material['MinimumStock']); ?></td>
                        <td><?php echo format_number($material['ReorderLevel']); ?></td>
                        <td>$<?php echo format_number($material['UnitCost']); ?></td>
                        <td>
                            <div class="action-buttons">
                                <button class="icon-btn" onclick='openEditModal(<?php echo json_encode($material); ?>)' title="Edit">
                                    <span class="material-icons">edit</span>
                                </button>
                                <button class="icon-btn danger" onclick="confirmDelete(<?php echo $material['MaterialID']; ?>, '<?php echo h($material['MaterialName']); ?>')" title="Delete">
                                    <span class="material-icons">delete</span>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <?php endwhile; ?>
                <?php else: ?>
                    <tr>
                        <td colspan="8" class="text-center">No raw materials found. Click "Add New Material" to get started.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Add Modal -->
<div id="addModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Add New Raw Material</h3>
            <button class="modal-close" onclick="closeModal('addModal')">&times;</button>
        </div>
        <form method="POST" action="">
            <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
            <input type="hidden" name="action" value="add">
            
            <div class="form-group">
                <label>Material Name <span class="required">*</span></label>
                <input type="text" name="material_name" class="form-control" required>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Category</label>
                    <select name="category" class="form-control">
                        <option value="">Select Category</option>
                        <option value="Fabric">Fabric</option>
                        <option value="Thread">Thread</option>
                        <option value="Buttons">Buttons</option>
                        <option value="Zippers">Zippers</option>
                        <option value="Packaging">Packaging</option>
                        <option value="Labels">Labels</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Unit <span class="required">*</span></label>
                    <select name="unit" class="form-control" required>
                        <option value="Meters">Meters</option>
                        <option value="Kilograms">Kilograms</option>
                        <option value="Pieces">Pieces</option>
                        <option value="Rolls">Rolls</option>
                        <option value="Boxes">Boxes</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Unit Cost (R)</label>
                    <input type="number" name="unit_cost" step="0.01" class="form-control" value="0">
                </div>
                <div class="form-group">
                    <label>Current Stock</label>
                    <input type="number" name="current_stock" step="0.01" class="form-control" value="0">
                </div>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Minimum Stock Level</label>
                    <input type="number" name="minimum_stock" step="0.01" class="form-control" value="0">
                </div>
                <div class="form-group">
                    <label>Reorder Level</label>
                    <input type="number" name="reorder_level" step="0.01" class="form-control" value="0">
                </div>
            </div>
            
            <div class="form-group">
                <label>Supplier Information</label>
                <textarea name="supplier_info" class="form-control" rows="2" placeholder="Supplier name, contact, lead time..."></textarea>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Add Material</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('addModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Modal -->
<div id="editModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Edit Raw Material</h3>
            <button class="modal-close" onclick="closeModal('editModal')">&times;</button>
        </div>
        <form method="POST" action="">
            <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="material_id" id="edit_material_id">
            
            <div class="form-group">
                <label>Material Name <span class="required">*</span></label>
                <input type="text" name="material_name" id="edit_material_name" class="form-control" required>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Category</label>
                    <select name="category" id="edit_category" class="form-control">
                        <option value="">Select Category</option>
                        <option value="Fabric">Fabric</option>
                        <option value="Thread">Thread</option>
                        <option value="Buttons">Buttons</option>
                        <option value="Zippers">Zippers</option>
                        <option value="Packaging">Packaging</option>
                        <option value="Labels">Labels</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Unit <span class="required">*</span></label>
                    <select name="unit" id="edit_unit" class="form-control" required>
                        <option value="Meters">Meters</option>
                        <option value="Kilograms">Kilograms</option>
                        <option value="Pieces">Pieces</option>
                        <option value="Rolls">Rolls</option>
                        <option value="Boxes">Boxes</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Unit Cost (R)</label>
                    <input type="number" name="unit_cost" id="edit_unit_cost" step="0.01" class="form-control">
                </div>
                <div class="form-group">
                    <label>Current Stock</label>
                    <input type="number" name="current_stock" id="edit_current_stock" step="0.01" class="form-control">
                </div>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Minimum Stock Level</label>
                    <input type="number" name="minimum_stock" id="edit_minimum_stock" step="0.01" class="form-control">
                </div>
                <div class="form-group">
                    <label>Reorder Level</label>
                    <input type="number" name="reorder_level" id="edit_reorder_level" step="0.01" class="form-control">
                </div>
            </div>
            
            <div class="form-group">
                <label>Supplier Information</label>
                <textarea name="supplier_info" id="edit_supplier_info" class="form-control" rows="2"></textarea>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Update Material</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('editModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" method="POST" style="display: none;">
    <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="material_id" id="delete_material_id">
</form>

<script>
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'flex';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function openEditModal(material) {
    document.getElementById('edit_material_id').value = material.MaterialID;
    document.getElementById('edit_material_name').value = material.MaterialName;
    document.getElementById('edit_category').value = material.Category || '';
    document.getElementById('edit_unit').value = material.Unit;
    document.getElementById('edit_unit_cost').value = material.UnitCost;
    document.getElementById('edit_current_stock').value = material.CurrentStock;
    document.getElementById('edit_minimum_stock').value = material.MinimumStock;
    document.getElementById('edit_reorder_level').value = material.ReorderLevel;
    document.getElementById('edit_supplier_info').value = material.SupplierInfo || '';
    openModal('editModal');
}

function confirmDelete(id, name) {
    if (confirm(`Are you sure you want to delete "${name}"? This action cannot be undone.`)) {
        document.getElementById('delete_material_id').value = id;
        document.getElementById('deleteForm').submit();
    }
}

window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
}
</script>

<style>
.action-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
}
.action-buttons {
    display: flex;
    gap: 8px;
}
.icon-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 6px;
}
.icon-btn .material-icons {
    font-size: 18px;
    color: #1a2634;
}
.icon-btn.danger:hover .material-icons {
    color: #c62828;
}
.modal {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0,0,0,0.5);
    z-index: 1100;
    align-items: center;
    justify-content: center;
}
.modal-content {
    background: white;
    border-radius: 16px;
    max-width: 600px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
}
.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid #e0e0e0;
}
.modal-close {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
}
.form-group {
    margin-bottom: 16px;
    padding: 0 24px;
}
.form-group label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
}
.form-group input, .form-group select, .form-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 6px;
}
.form-row.two-columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
    padding: 0 24px;
}
.form-actions {
    display: flex;
    gap: 12px;
    padding: 20px 24px;
    border-top: 1px solid #e0e0e0;
    margin-top: 16px;
}
.btn {
    padding: 10px 20px;
    border-radius: 6px;
    border: none;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 8px;
}
.btn-primary {
    background: #1a2634;
    color: white;
}
.btn-secondary {
    background: #e0e0e0;
    color: #333;
}
.required {
    color: #c62828;
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
.text-center {
    text-align: center;
    padding: 20px;
}
.card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    overflow: hidden;
}
.card-header {
    padding: 16px 20px;
    border-bottom: 1px solid #e0e0e0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.card-header h3 {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    margin: 0;
}
.card-header .material-icons {
    color: #c9a03d;
}
.mt-2 {
    margin-top: 20px;
}
.table-container {
    overflow-x: auto;
}
.data-table {
    width: 100%;
    border-collapse: collapse;
}
.data-table th {
    background: #1a2634;
    color: white;
    padding: 12px 16px;
    text-align: left;
    font-size: 13px;
}
.data-table td {
    padding: 10px 16px;
    border-bottom: 1px solid #eee;
    font-size: 14px;
}
.data-table tr:hover {
    background: #f8f9fa;
}
</style>

<?php include "../includes/footer.php"; ?>