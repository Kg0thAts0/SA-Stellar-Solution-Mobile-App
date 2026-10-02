<?php
// Side Navigation Bar - Include after header.php
// No need for extra <?php tag here since it's already in PHP mode from header.php
?>

<aside class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <img src="../assets/logo-small.jpg" alt="Logo" class="sidebar-logo">
        <h3>QA Factory</h3>
    </div>
    
    <nav class="sidebar-nav">
        <ul class="sidebar-menu">
            <!-- Dashboard - Fixed path -->
            <li class="sidebar-item">
                <?php
                // Determine correct dashboard path based on role
                $dashboard_path = '';
                switch ($_SESSION['role'] ?? '') {
                    case 'Admin':
                        $dashboard_path = '../admin/dashboard.php';
                        break;
                    case 'ProductionManager':
                        $dashboard_path = '../production/dashboard.php';
                        break;
                    case 'QualityController':
                        $dashboard_path = '../quality/dashboard.php';
                        break;
                    case 'InventoryClerk':
                        $dashboard_path = '../inventory/dashboard.php';
                        break;
                    case 'Supervisor':
                        $dashboard_path = '../supervisor/dashboard.php';
                        break;
                    default:
                        $dashboard_path = '../index.php';
                }
                ?>
                <a href="<?php echo $dashboard_path; ?>" class="sidebar-link">
                    <span class="material-icons">dashboard</span>
                    <span class="sidebar-text">Dashboard</span>
                </a>
            </li>
            
            <!-- Admin Menu -->
            <?php if (isset($_SESSION['role']) && $_SESSION['role'] === 'Admin'): ?>
            <li class="sidebar-divider">Administration</li>
            <li class="sidebar-item">
                <a href="../admin/add_employee.php" class="sidebar-link">
                    <span class="material-icons">person_add</span>
                    <span class="sidebar-text">Add Employee</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../admin/manage_employees.php" class="sidebar-link">
                    <span class="material-icons">manage_accounts</span>
                    <span class="sidebar-text">Manage Employees</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../admin/system_reports.php" class="sidebar-link">
                    <span class="material-icons">bar_chart</span>
                    <span class="sidebar-text">System Reports</span>
                </a>
            </li>
            <?php endif; ?>
            
            <!-- Inventory Clerk Menu -->
            <?php if (isset($_SESSION['role']) && $_SESSION['role'] === 'InventoryClerk'): ?>
            <li class="sidebar-divider">Inventory Management</li>
            <li class="sidebar-item">
                <a href="../inventory/raw_materials.php" class="sidebar-link">
                    <span class="material-icons">inventory_2</span>
                    <span class="sidebar-text">Raw Materials</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../inventory/finished_goods.php" class="sidebar-link">
                    <span class="material-icons">checkroom</span>
                    <span class="sidebar-text">Finished Goods</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../inventory/stock_levels.php" class="sidebar-link">
                    <span class="material-icons">warehouse</span>
                    <span class="sidebar-text">Stock Levels</span>
                </a>
            </li>
            <?php endif; ?>
            
            <!-- Production Manager Menu -->
            <?php if (isset($_SESSION['role']) && $_SESSION['role'] === 'ProductionManager'): ?>
            <li class="sidebar-divider">Production</li>
            <li class="sidebar-item">
                <a href="../production/record_output.php" class="sidebar-link">
                    <span class="material-icons">factory</span>
                    <span class="sidebar-text">Record Output</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../production/performance.php" class="sidebar-link">
                    <span class="material-icons">speed</span>
                    <span class="sidebar-text">Line Performance</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../production/inventory.php" class="sidebar-link">
                    <span class="material-icons">inventory</span>
                    <span class="sidebar-text">Inventory Levels</span>
                </a>
            </li>
            <?php endif; ?>
            
            <!-- Quality Controller Menu -->
            <?php if (isset($_SESSION['role']) && $_SESSION['role'] === 'QualityController'): ?>
            <li class="sidebar-divider">Quality Control</li>
            <li class="sidebar-item">
                <a href="../quality/log_inspection.php" class="sidebar-link">
                    <span class="material-icons">fact_check</span>
                    <span class="sidebar-text">Log Inspections</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../quality/qc_batches.php" class="sidebar-link">
                    <span class="material-icons">approval</span>
                    <span class="sidebar-text">Approve Batches</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../quality/qc_history.php" class="sidebar-link">
                    <span class="material-icons">history</span>
                    <span class="sidebar-text">QC History</span>
                </a>
            </li>
            <?php endif; ?>
            
            <!-- Supervisor Menu -->
            <?php if (isset($_SESSION['role']) && $_SESSION['role'] === 'Supervisor'): ?>
            <li class="sidebar-divider">Supervision</li>
            <li class="sidebar-item">
                <a href="../supervisor/production_overview.php" class="sidebar-link">
                    <span class="material-icons">visibility</span>
                    <span class="sidebar-text">Production Overview</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../supervisor/qc_issues.php" class="sidebar-link">
                    <span class="material-icons">report_problem</span>
                    <span class="sidebar-text">QC Issues</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="../supervisor/inventory_snapshot.php" class="sidebar-link">
                    <span class="material-icons">snapshot</span>
                    <span class="sidebar-text">Inventory Snapshot</span>
                </a>
            </li>
            <?php endif; ?>
            
            <!-- Common Menu Items -->
            <li class="sidebar-divider">Account</li>
            <li class="sidebar-item">
                <a href="../profile.php" class="sidebar-link">
                    <span class="material-icons">account_circle</span>
                    <span class="sidebar-text">My Profile</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="#" onclick="document.getElementById('logoutForm').submit(); return false;" class="sidebar-link">
                    <span class="material-icons">logout</span>
                    <span class="sidebar-text">Logout</span>
                </a>
            </li>
        </ul>
    </nav>
</aside>

<!-- Hidden Logout Form -->
<form id="logoutForm" method="POST" action="../logout.php" style="display: none;">
    <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token'] ?? ''; ?>">
</form>

<script>
// Sidebar toggle functionality
document.getElementById('sidebarToggle')?.addEventListener('click', function() {
    document.getElementById('sidebar')?.classList.toggle('collapsed');
    document.body.classList.toggle('sidebar-collapsed');
});

// Close sidebar on mobile when clicking a link
document.querySelectorAll('.sidebar-link').forEach(link => {
    link.addEventListener('click', () => {
        if (window.innerWidth <= 768) {
            document.getElementById('sidebar')?.classList.remove('active');
        }
    });
});
</script>