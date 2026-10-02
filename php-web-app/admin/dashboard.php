<?php
require_once "../includes/auth.php";
require_role('Admin');
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Admin Dashboard</h1>
    <p>Welcome back, <strong><?php echo h($_SESSION['full_name']); ?></strong>. Manage your factory operations from here.</p>
</div>

<div class="dashboard-grid">
    <a href="add_employee.php" class="dashboard-card">
        <span class="material-icons">person_add</span>
        <h3>Add New Employee</h3>
        <p>Register new staff members to the system</p>
    </a>
    
    <a href="manage_employees.php" class="dashboard-card">
        <span class="material-icons">manage_accounts</span>
        <h3>Employee Management</h3>
        <p>View, update, and manage employee records</p>
    </a>
    
    <a href="system_reports.php" class="dashboard-card">
        <span class="material-icons">assessment</span>
        <h3>System Reports</h3>
        <p>Generate and view system-wide reports</p>
    </a>
</div>
<!-- Add this to the bottom of admin/dashboard.php before footer -->
<div class="dashboard-grid mt-2">
    <div class="card">
        <div class="card-header">
            <h3><span class="material-icons">trending_up</span> Monthly Production Trend</h3>
        </div>
        <div class="card-body">
            <canvas id="productionChart" height="250"></canvas>
        </div>
    </div>
    <div class="card">
        <div class="card-header">
            <h3><span class="material-icons">verified</span> Quality Pass Rate</h3>
        </div>
        <div class="card-body">
            <canvas id="qualityChart" height="250"></canvas>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
// Production Chart
fetch('../api/stats.php?type=production')
    .then(response => response.json())
    .then(data => {
        new Chart(document.getElementById('productionChart'), {
            type: 'line',
            data: {
                labels: data.months,
                datasets: [{
                    label: 'Units Produced',
                    data: data.totals,
                    borderColor: '#c9a03d',
                    backgroundColor: 'rgba(201, 160, 61, 0.1)',
                    tension: 0.3,
                    fill: true
                }]
            },
            options: { responsive: true, maintainAspectRatio: true }
        });
    });

// Quality Chart
fetch('../api/stats.php?type=quality')
    .then(response => response.json())
    .then(data => {
        new Chart(document.getElementById('qualityChart'), {
            type: 'doughnut',
            data: {
                labels: ['Pass', 'Fail', 'Conditional'],
                datasets: [{
                    data: [data.pass, data.fail, data.conditional],
                    backgroundColor: ['#2e7d32', '#c62828', '#ed6c02']
                }]
            },
            options: { responsive: true, maintainAspectRatio: true }
        });
    });
</script>


<?php include "../includes/footer.php"; ?>