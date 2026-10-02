<?php
require_once "../includes/auth.php";
require_role('Admin');
require_once "../connect_db.php";

// Handle status update
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['action']) && $_POST['action'] === 'update_status') {
    validate_csrf();
    $id = intval($_POST['employee_id']);
    $status = $_POST['status'];
    
    $allowed_statuses = ['Pending', 'Active', 'Inactive'];
    if (!in_array($status, $allowed_statuses)) {
        $_SESSION['error'] = "Invalid status value.";
        header("Location: manage_employees.php");
        exit();
    }
    
    if ($id == $_SESSION['employee_id']) {
        $_SESSION['error'] = "You cannot change your own status.";
        header("Location: manage_employees.php");
        exit();
    }
    
    $stmt = $conn->prepare("UPDATE employee SET EmployeeStatus = ? WHERE EmployeeID = ?");
    $stmt->bind_param("si", $status, $id);
    $stmt->execute();
    $_SESSION['success'] = "Status updated.";
    header("Location: manage_employees.php");
    exit();
}

// Handle delete
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['action']) && $_POST['action'] === 'delete') {
    validate_csrf();
    $id = intval($_POST['employee_id']);
    
    if ($id == $_SESSION['employee_id']) {
        $_SESSION['error'] = "You cannot delete your own account.";
        header("Location: manage_employees.php");
        exit();
    }
    
    $stmt = $conn->prepare("DELETE FROM employee WHERE EmployeeID = ?");
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $_SESSION['success'] = "Employee deleted.";
    header("Location: manage_employees.php");
    exit();
}

// Get search and filter parameters
$search = isset($_GET['search']) ? trim($_GET['search']) : '';
$role_filter = isset($_GET['role']) ? $_GET['role'] : '';
$status_filter = isset($_GET['status']) ? $_GET['status'] : '';

// Build query
$sql = "SELECT EmployeeID, FullName, EmailAddress, Role, EmployeeStatus, CreatedAt FROM employee WHERE Role != 'Admin'";
$params = [];
$types = "";

if (!empty($search)) {
    $sql .= " AND (FullName LIKE ? OR EmailAddress LIKE ?)";
    $search_param = "%$search%";
    $params[] = $search_param;
    $params[] = $search_param;
    $types .= "ss";
}

if (!empty($role_filter)) {
    $sql .= " AND Role = ?";
    $params[] = $role_filter;
    $types .= "s";
}

if (!empty($status_filter)) {
    $sql .= " AND EmployeeStatus = ?";
    $params[] = $status_filter;
    $types .= "s";
}

$sql .= " ORDER BY FullName ASC";
$stmt = $conn->prepare($sql);
if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}
$stmt->execute();
$employees = $stmt->get_result();

// Get filter counts
$role_counts = [];
$status_counts = [];
$count_sql = "SELECT Role, COUNT(*) as count FROM employee WHERE Role != 'Admin' GROUP BY Role";
$count_result = $conn->query($count_sql);
while ($row = $count_result->fetch_assoc()) {
    $role_counts[$row['Role']] = $row['count'];
}
$count_sql2 = "SELECT EmployeeStatus, COUNT(*) as count FROM employee WHERE Role != 'Admin' GROUP BY EmployeeStatus";
$count_result2 = $conn->query($count_sql2);
while ($row = $count_result2->fetch_assoc()) {
    $status_counts[$row['EmployeeStatus']] = $row['count'];
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Manage Employees</h1>
    <p>View, update status, and manage employee records</p>
</div>

<!-- Search and Filter Bar -->
<div class="filter-bar">
    <div class="search-box">
        <span class="material-icons">search</span>
        <input type="text" id="searchInput" placeholder="Search by name or email..." value="<?php echo h($search); ?>">
    </div>
    
    <div class="filter-group">
        <select id="roleFilter">
            <option value="">All Roles</option>
            <option value="ProductionManager" <?php echo $role_filter == 'ProductionManager' ? 'selected' : ''; ?>>Production Manager (<?php echo $role_counts['ProductionManager'] ?? 0; ?>)</option>
            <option value="QualityController" <?php echo $role_filter == 'QualityController' ? 'selected' : ''; ?>>Quality Controller (<?php echo $role_counts['QualityController'] ?? 0; ?>)</option>
            <option value="InventoryClerk" <?php echo $role_filter == 'InventoryClerk' ? 'selected' : ''; ?>>Inventory Clerk (<?php echo $role_counts['InventoryClerk'] ?? 0; ?>)</option>
            <option value="Supervisor" <?php echo $role_filter == 'Supervisor' ? 'selected' : ''; ?>>Supervisor (<?php echo $role_counts['Supervisor'] ?? 0; ?>)</option>
        </select>
    </div>
    
    <div class="filter-group">
        <select id="statusFilter">
            <option value="">All Status</option>
            <option value="Active" <?php echo $status_filter == 'Active' ? 'selected' : ''; ?>>Active (<?php echo $status_counts['Active'] ?? 0; ?>)</option>
            <option value="Pending" <?php echo $status_filter == 'Pending' ? 'selected' : ''; ?>>Pending (<?php echo $status_counts['Pending'] ?? 0; ?>)</option>
            <option value="Inactive" <?php echo $status_filter == 'Inactive' ? 'selected' : ''; ?>>Inactive (<?php echo $status_counts['Inactive'] ?? 0; ?>)</option>
        </select>
    </div>
    
    <button class="btn btn-outline" id="resetFilters">
        <span class="material-icons">refresh</span>
        Reset
    </button>
    
    <button class="btn btn-outline" onclick="exportToCSV()">
        <span class="material-icons">download</span>
        Export CSV
    </button>
    
    <a href="add_employee.php" class="btn btn-primary">
        <span class="material-icons">person_add</span>
        Add Employee
    </a>
</div>

<div class="card mt-2">
    <div class="table-container">
        <table class="data-table" id="employeesTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Date Added</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="tableBody">
                <?php if ($employees->num_rows > 0): ?>
                    <?php while ($emp = $employees->fetch_assoc()): ?>
                    <tr>
                        <td><?php echo h($emp['EmployeeID']); ?></td>
                        <td><?php echo h($emp['FullName']); ?></td>
                        <td><?php echo h($emp['EmailAddress']); ?></td>
                        <td><?php echo h($emp['Role']); ?></td>
                        <td><span class="badge badge-<?php echo strtolower($emp['EmployeeStatus']); ?>"><?php echo h($emp['EmployeeStatus']); ?></span></td>
                        <td><?php echo date('d M Y', strtotime($emp['CreatedAt'])); ?></td>
                        <td>
                            <div class="action-buttons">
                                <form method="POST" style="display: inline-flex; gap: 4px;">
                                    <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
                                    <input type="hidden" name="action" value="update_status">
                                    <input type="hidden" name="employee_id" value="<?php echo $emp['EmployeeID']; ?>">
                                    <select name="status" class="status-select" onchange="this.form.submit()">
                                        <option value="Pending" <?php echo $emp['EmployeeStatus'] == 'Pending' ? 'selected' : ''; ?>>Pending</option>
                                        <option value="Active" <?php echo $emp['EmployeeStatus'] == 'Active' ? 'selected' : ''; ?>>Active</option>
                                        <option value="Inactive" <?php echo $emp['EmployeeStatus'] == 'Inactive' ? 'selected' : ''; ?>>Inactive</option>
                                    </select>
                                </form>
                                <form method="POST" style="display: inline;" onsubmit="return confirm('Delete <?php echo h($emp['FullName']); ?>?')">
                                    <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="employee_id" value="<?php echo $emp['EmployeeID']; ?>">
                                    <button type="submit" class="icon-btn danger"><span class="material-icons">delete</span></button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    <?php endwhile; ?>
                <?php else: ?>
                    <tr><td colspan="7" class="text-center">No employees found.</td></tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<script>
function applyFilters() {
    const search = document.getElementById('searchInput').value;
    const role = document.getElementById('roleFilter').value;
    const status = document.getElementById('statusFilter').value;
    window.location.href = `?search=${encodeURIComponent(search)}&role=${role}&status=${status}`;
}

document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') applyFilters();
});
document.getElementById('roleFilter').addEventListener('change', applyFilters);
document.getElementById('statusFilter').addEventListener('change', applyFilters);
document.getElementById('resetFilters').addEventListener('click', function() {
    window.location.href = window.location.pathname;
});

function exportToCSV() {
    const rows = document.querySelectorAll('#employeesTable tr');
    let csv = [];
    rows.forEach(row => {
        const cells = row.querySelectorAll('th, td');
        const rowData = Array.from(cells).slice(0, -1).map(cell => `"${cell.innerText.replace(/"/g, '""')}"`);
        if (rowData.length) csv.push(rowData.join(','));
    });
    const blob = new Blob([csv.join('\n')], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'employees_export.csv';
    a.click();
    URL.revokeObjectURL(url);
}
</script>

<style>
.filter-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    flex-wrap: wrap;
    align-items: center;
}
.search-box {
    display: flex;
    align-items: center;
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 0 12px;
    flex: 1;
    min-width: 200px;
}
.search-box .material-icons { color: #6c757d; font-size: 20px; }
.search-box input {
    border: none;
    padding: 10px;
    width: 100%;
    outline: none;
}
.filter-group select {
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: white;
    min-width: 150px;
}
.action-buttons { display: flex; gap: 8px; align-items: center; }
.icon-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 6px;
}
.icon-btn .material-icons { font-size: 18px; color: #6c757d; }
.icon-btn.danger:hover .material-icons { color: #c62828; }
.status-select { padding: 6px 10px; border-radius: 6px; border: 1px solid #ddd; }
</style>

<?php include "../includes/footer.php"; ?>