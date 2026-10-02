<?php
require_once "../includes/auth.php";
require_role('InventoryClerk');
require_once "../connect_db.php";

// Helper function to generate product code - FIXED
function generate_product_code() {
    global $conn;
    $prefix = 'FG';
    
    // Get the latest ProductCode (not ProductID)
    $sql = "SELECT ProductCode FROM finished_good ORDER BY ProductCode DESC LIMIT 1";
    $result = $conn->query($sql);
    
    if ($result && $result->num_rows > 0) {
        $last = $result->fetch_assoc();
        $code = $last['ProductCode']; // e.g., "FG00001"
        // Extract the number part (remove "FG")
        $number = intval(substr($code, 2)) + 1;
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
        header("Location: finished_goods.php");
        exit();
    }
    
    $product_code = generate_product_code();
    $product_name = trim($_POST['product_name'] ?? '');
    $category = trim($_POST['category'] ?? '');
    $unit = $_POST['unit'] ?? 'Pieces';
    $current_stock = floatval($_POST['current_stock'] ?? 0);
    $unit_price = floatval($_POST['unit_price'] ?? 0);
    $production_batch_id = trim($_POST['production_batch_id'] ?? '');
    
    // Validate required fields
    if (empty($product_name)) {
        $_SESSION['error'] = "Product name is required.";
        header("Location: finished_goods.php");
        exit();
    }
    
    if (empty($unit)) {
        $_SESSION['error'] = "Unit is required.";
        header("Location: finished_goods.php");
        exit();
    }
    
    // Insert with all columns
    $sql = "INSERT INTO finished_good (ProductCode, ProductName, Category, Unit, CurrentStock, UnitPrice, ProductionBatchID) 
            VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        $_SESSION['error'] = "Prepare failed: " . $conn->error;
        header("Location: finished_goods.php");
        exit();
    }
    
    // 7 parameters: 5 strings (s), 2 doubles (d)
    $stmt->bind_param("ssssdds", $product_code, $product_name, $category, $unit, $current_stock, $unit_price, $production_batch_id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Finished good added successfully. Code: $product_code";
    } else {
        $_SESSION['error'] = "Failed to add finished good: " . $stmt->error;
    }
    $stmt->close();
    header("Location: finished_goods.php");
    exit();
}

// Handle Edit operation
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['action']) && $_POST['action'] === 'edit') {
    validate_csrf();
    
    $product_id = intval($_POST['product_id']);
    $product_name = trim($_POST['product_name']);
    $category = trim($_POST['category']);
    $unit = $_POST['unit'];
    $current_stock = floatval($_POST['current_stock']);
    $unit_price = floatval($_POST['unit_price']);
    
    $sql = "UPDATE finished_good SET ProductName=?, Category=?, Unit=?, CurrentStock=?, UnitPrice=? WHERE ProductID=?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssddi", $product_name, $category, $unit, $current_stock, $unit_price, $product_id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Finished good updated successfully.";
    } else {
        $_SESSION['error'] = "Failed to update finished good: " . $stmt->error;
    }
    $stmt->close();
    header("Location: finished_goods.php");
    exit();
}

// Handle Delete operation
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['action']) && $_POST['action'] === 'delete') {
    validate_csrf();
    
    $product_id = intval($_POST['product_id']);
    
    $sql = "DELETE FROM finished_good WHERE ProductID = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $product_id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Finished good deleted successfully.";
    } else {
        $_SESSION['error'] = "Failed to delete finished good: " . $stmt->error;
    }
    $stmt->close();
    header("Location: finished_goods.php");
    exit();
}

// Fetch all finished goods
$sql = "SELECT * FROM finished_good ORDER BY ProductName ASC";
$products = $conn->query($sql);

// Calculate total value
$total_value = 0;
$products_list = [];
if ($products && $products->num_rows > 0) {
    while ($product = $products->fetch_assoc()) {
        $products_list[] = $product;
        $total_value += $product['CurrentStock'] * $product['UnitPrice'];
    }
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Finished Goods Management</h1>
    <p>Manage finished products inventory and pricing</p>
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
        Add New Product
    </button>
    <button class="btn btn-outline" onclick="exportToCSV()">
        <span class="material-icons">download</span>
        Export CSV
    </button>
</div>

<div class="card mt-2">
    <div class="table-container">
        <table class="data-table" id="productsTable">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Product Name</th>
                    <th>Category</th>
                    <th>Current Stock</th>
                    <th>Unit Price</th>
                    <th>Total Value</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (!empty($products_list)): ?>
                    <?php foreach ($products_list as $product): ?>
                    <tr>
                        <td><?php echo h($product['ProductCode']); ?></td>
                        <td><?php echo h($product['ProductName']); ?></td>
                        <td><?php echo h($product['Category'] ?: '-'); ?></td>
                        <td><?php echo number_format($product['CurrentStock']); ?> <?php echo h($product['Unit']); ?></td>
                        <td>R<?php echo number_format($product['UnitPrice'], 2); ?></td>
                        <td>R<?php echo number_format($product['CurrentStock'] * $product['UnitPrice'], 2); ?></td>
                        <td>
                            <div class="action-buttons">
                                <button class="icon-btn" onclick='openEditModal(<?php echo htmlspecialchars(json_encode($product), ENT_QUOTES, 'UTF-8'); ?>)' title="Edit">
                                    <span class="material-icons">edit</span>
                                </button>
                                <button class="icon-btn danger" onclick="confirmDelete(<?php echo $product['ProductID']; ?>, '<?php echo h($product['ProductName']); ?>')" title="Delete">
                                    <span class="material-icons">delete</span>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <?php endforeach; ?>
                <?php else: ?>
                    <tr>
                        <td colspan="7" class="text-center">No products found. Click "Add New Product" to get started.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
            <?php if (!empty($products_list)): ?>
            <tfoot>
                <tr class="total-row">
                    <td colspan="5"><strong>Total Inventory Value</strong></td>
                    <td colspan="2">
                        <strong>R<?php echo number_format($total_value, 2); ?></strong>
                    </td>
                </tr>
            </tfoot>
            <?php endif; ?>
        </table>
    </div>
</div>

<!-- Add Modal -->
<div id="addModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Add New Finished Good</h3>
            <button class="modal-close" onclick="closeModal('addModal')">&times;</button>
        </div>
        <form method="POST" action="">
            <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
            <input type="hidden" name="action" value="add">
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Product Name <span class="required">*</span></label>
                    <input type="text" name="product_name" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Category</label>
                    <select name="category" class="form-control">
                        <option value="">Select Category</option>
                        <option value="T-Shirts">T-Shirts</option>
                        <option value="Pants">Pants</option>
                        <option value="Dresses">Dresses</option>
                        <option value="Jackets">Jackets</option>
                        <option value="Uniforms">Uniforms</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Unit <span class="required">*</span></label>
                    <select name="unit" class="form-control" required>
                        <option value="Pieces">Pieces</option>
                        <option value="Boxes">Boxes</option>
                        <option value="Packs">Packs</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Unit Price (R)</label>
                    <input type="number" name="unit_price" step="0.01" class="form-control" value="0">
                </div>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Initial Stock Quantity</label>
                    <input type="number" name="current_stock" step="1" class="form-control" value="0">
                </div>
                <div class="form-group">
                    <label>Production Batch ID (Optional)</label>
                    <input type="text" name="production_batch_id" class="form-control" placeholder="e.g., BATCH-20241201-001">
                </div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Add Product</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('addModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Modal -->
<div id="editModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Edit Finished Good</h3>
            <button class="modal-close" onclick="closeModal('editModal')">&times;</button>
        </div>
        <form method="POST" action="">
            <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="product_id" id="edit_product_id">
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Product Name <span class="required">*</span></label>
                    <input type="text" name="product_name" id="edit_product_name" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Category</label>
                    <select name="category" id="edit_category" class="form-control">
                        <option value="">Select Category</option>
                        <option value="T-Shirts">T-Shirts</option>
                        <option value="Pants">Pants</option>
                        <option value="Dresses">Dresses</option>
                        <option value="Jackets">Jackets</option>
                        <option value="Uniforms">Uniforms</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row two-columns">
                <div class="form-group">
                    <label>Unit <span class="required">*</span></label>
                    <select name="unit" id="edit_unit" class="form-control" required>
                        <option value="Pieces">Pieces</option>
                        <option value="Boxes">Boxes</option>
                        <option value="Packs">Packs</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Unit Price (R)</label>
                    <input type="number" name="unit_price" id="edit_unit_price" step="0.01" class="form-control">
                </div>
            </div>
            
            <div class="form-group">
                <label>Current Stock</label>
                <input type="number" name="current_stock" id="edit_current_stock" step="1" class="form-control">
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Changes</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('editModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Delete Confirmation Form -->
<form id="deleteForm" method="POST" style="display: none;">
    <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="product_id" id="delete_product_id">
</form>

<script>
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'flex';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function openEditModal(product) {
    document.getElementById('edit_product_id').value = product.ProductID;
    document.getElementById('edit_product_name').value = product.ProductName;
    document.getElementById('edit_category').value = product.Category || '';
    document.getElementById('edit_unit').value = product.Unit;
    document.getElementById('edit_unit_price').value = product.UnitPrice;
    document.getElementById('edit_current_stock').value = product.CurrentStock;
    openModal('editModal');
}

function confirmDelete(id, name) {
    if (confirm(`Are you sure you want to delete "${name}"? This action cannot be undone.`)) {
        document.getElementById('delete_product_id').value = id;
        document.getElementById('deleteForm').submit();
    }
}

function exportToCSV() {
    const table = document.getElementById('productsTable');
    const rows = table.querySelectorAll('tr');
    let csv = [];
    
    rows.forEach(row => {
        const cells = row.querySelectorAll('th, td');
        const rowData = Array.from(cells).slice(0, -1).map(cell => {
            let text = cell.innerText;
            if (text.includes('edit') || text.includes('delete')) {
                return '';
            }
            return `"${text.replace(/"/g, '""')}"`;
        }).filter(t => t !== '');
        if (rowData.length > 0) {
            csv.push(rowData.join(','));
        }
    });
    
    const blob = new Blob([csv.join('\n')], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'finished_goods_export.csv';
    a.click();
    URL.revokeObjectURL(url);
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
    flex-wrap: wrap;
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
.form-group input, .form-group select {
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
.btn-outline {
    background: transparent;
    border: 1px solid #c9a03d;
    color: #c9a03d;
}
.btn-outline:hover {
    background: #c9a03d;
    color: white;
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
.total-row {
    background: #f8f9fa;
}
.total-row td {
    font-weight: bold;
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