<?php
require_once "../includes/auth.php";
require_role('Admin');
require_once "../connect_db.php";

// Pagination
$page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
$limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 20;
$offset = ($page - 1) * $limit;

// Filters
$action_filter = isset($_GET['action']) ? $_GET['action'] : '';
$employee_filter = isset($_GET['employee']) ? $_GET['employee'] : '';
$date_filter = isset($_GET['date']) ? $_GET['date'] : '';

// Build query
$sql = "SELECT * FROM audit_log WHERE 1=1";
$count_sql = "SELECT COUNT(*) as total FROM audit_log WHERE 1=1";
$params = [];
$types = "";

if (!empty($action_filter)) {
    $sql .= " AND Action = ?";
    $count_sql .= " AND Action = ?";
    $params[] = $action_filter;
    $types .= "s";
}

if (!empty($employee_filter)) {
    $sql .= " AND EmployeeName LIKE ?";
    $count_sql .= " AND EmployeeName LIKE ?";
    $params[] = "%$employee_filter%";
    $types .= "s";
}

if (!empty($date_filter)) {
    $sql .= " AND DATE(Timestamp) = ?";
    $count_sql .= " AND DATE(Timestamp) = ?";
    $params[] = $date_filter;
    $types .= "s";
}

$sql .= " ORDER BY Timestamp DESC LIMIT $offset, $limit";

// Get total count
$stmt = $conn->prepare($count_sql);
if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}
$stmt->execute();
$total_result = $stmt->get_result();
$total_rows = $total_result->fetch_assoc()['total'];
$total_pages = ceil($total_rows / $limit);

// Get audit logs
$stmt = $conn->prepare($sql);
if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}
$stmt->execute();
$logs = $stmt->get_result();

// Get unique actions for filter
$actions = $conn->query("SELECT DISTINCT Action FROM audit_log ORDER BY Action");
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Activity Log (Audit Trail)</h1>
    <p>View all user activities and system events</p>
</div>

<!-- Filter Bar -->
<div class="filter-bar">
    <div class="filter-group">
        <select id="actionFilter">
            <option value="">All Actions</option>
            <?php while ($action = $actions->fetch_assoc()): ?>
            <option value="<?php echo h($action['Action']); ?>" <?php echo $action_filter == $action['Action'] ? 'selected' : ''; ?>>
                <?php echo h($action['Action']); ?>
            </option>
            <?php endwhile; ?>
        </select>
    </div>
    
    <div class="filter-group">
        <input type="text" id="employeeFilter" placeholder="Search by employee..." value="<?php echo h($employee_filter); ?>">
    </div>
    
    <div class="filter-group">
        <input type="date" id="dateFilter" value="<?php echo h($date_filter); ?>">
    </div>
    
    <button class="btn btn-primary" id="applyFilters">
        <span class="material-icons">search</span> Apply
    </button>
    
    <button class="btn btn-outline" id="resetFilters">
        <span class="material-icons">refresh</span> Reset
    </button>
    
    <button class="btn btn-outline" onclick="exportToCSV()">
        <span class="material-icons">download</span> Export CSV
    </button>
</div>

<div class="card mt-2">
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Timestamp</th>
                    <th>Employee</th>
                    <th>Action</th>
                    <th>Table</th>
                    <th>Record ID</th>
                    <th>Details</th>
                    <th>IP Address</th>
                </tr>
            </thead>
            <tbody>
                <?php if ($logs->num_rows > 0): ?>
                    <?php while ($log = $logs->fetch_assoc()): ?>
                    <tr>
                        <td><?php echo date('d M Y H:i:s', strtotime($log['Timestamp'])); ?></td>
                        <td><?php echo h($log['EmployeeName']); ?> (ID: <?php echo $log['EmployeeID']; ?>)</td>
                        <td>
                            <span class="badge badge-<?php 
                                if (strpos($log['Action'], 'Success') !== false) echo 'success';
                                elseif (strpos($log['Action'], 'Failed') !== false) echo 'error';
                                elseif (strpos($log['Action'], 'Delete') !== false) echo 'danger';
                                elseif (strpos($log['Action'], 'Update') !== false) echo 'warning';
                                else echo 'info';
                            ?>">
                                <?php echo h($log['Action']); ?>
                            </span>
                         </td>
                        <td><?php echo h($log['TableName'] ?: '-'); ?></td>
                        <td><?php echo h($log['RecordID'] ?: '-'); ?></td>
                        <td class="details-cell">
                            <?php if ($log['OldValue']): ?>
                            <span class="old-value">Old: <?php echo h($log['OldValue']); ?></span><br>
                            <?php endif; ?>
                            <?php if ($log['NewValue']): ?>
                            <span class="new-value">New: <?php echo h($log['NewValue']); ?></span>
                            <?php endif; ?>
                        </td>
                        <td><?php echo h($log['IPAddress']); ?></td>
                    </tr>
                    <?php endwhile; ?>
                <?php else: ?>
                    <tr><td colspan="7" class="text-center">No activity logs found.</td></tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Pagination -->
<?php
$base_url = "?";
if ($action_filter) $base_url .= "action=$action_filter&";
if ($employee_filter) $base_url .= "employee=" . urlencode($employee_filter) . "&";
if ($date_filter) $base_url .= "date=$date_filter&";
include "../includes/pagination.php";
echo renderPagination($page, $total_pages, rtrim($base_url, '&'));
?>

<script>
document.getElementById('applyFilters')?.addEventListener('click', function() {
    const action = document.getElementById('actionFilter').value;
    const employee = document.getElementById('employeeFilter').value;
    const date = document.getElementById('dateFilter').value;
    window.location.href = `?action=${encodeURIComponent(action)}&employee=${encodeURIComponent(employee)}&date=${date}`;
});

document.getElementById('resetFilters')?.addEventListener('click', function() {
    window.location.href = window.location.pathname;
});

function exportToCSV() {
    const rows = document.querySelectorAll('.data-table tr');
    let csv = [];
    rows.forEach(row => {
        const cells = row.querySelectorAll('th, td');
        const rowData = Array.from(cells).slice(0, -1).map(cell => `"${cell.innerText.replace(/"/g, '""')}"`);
        if (rowData.length > 0) csv.push(rowData.join(','));
    });
    const blob = new Blob([csv.join('\n')], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'audit_log_export.csv';
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
.filter-group input, .filter-group select {
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 6px;
    min-width: 150px;
}
.details-cell {
    max-width: 250px;
    font-size: 11px;
}
.old-value { color: #c62828; }
.new-value { color: #2e7d32; }
.badge-success { background: #e8f5e9; color: #2e7d32; }
.badge-error { background: #ffebee; color: #c62828; }
.badge-danger { background: #ffebee; color: #c62828; }
.badge-warning { background: #fff4e5; color: #ed6c02; }
.badge-info { background: #e3f2fd; color: #0288d1; }
</style>

<?php include "../includes/footer.php"; ?>