<?php
require_once "../includes/auth.php";
require_role('Admin');
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Add New Employee</h1>
    <p>Register a new staff member to the QA Clothing Factory System</p>
</div>

<div class="form-container">
    <form method="POST" action="add_employee_process.php" class="app-form" id="employeeForm">
        <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
        
        <div class="form-row two-columns">
            <div class="form-group">
                <label>Full Name <span class="required">*</span></label>
                <input type="text" name="fullname" class="form-control" required 
                       placeholder="e.g., Thabo Mbeki">
            </div>
            
            <div class="form-group">
                <label>Email Address <span class="required">*</span></label>
                <input type="email" name="email" class="form-control" required 
                       placeholder="employee@qafactory.co.za">
            </div>
        </div>
        
        <div class="form-row two-columns">
            <div class="form-group">
                <label>Password <span class="required">*</span></label>
                <input type="password" name="password" id="password" class="form-control" required 
                       minlength="8">
                <small>Minimum 8 characters</small>
            </div>
            
            <div class="form-group">
                <label>Confirm Password <span class="required">*</span></label>
                <input type="password" name="confirm_password" id="confirm_password" class="form-control" required>
                <span id="password_match_msg" style="font-size: 11px;"></span>
            </div>
        </div>
        
        <div class="form-row two-columns">
            <div class="form-group">
                <label>Role <span class="required">*</span></label>
                <select name="role" class="form-control" required>
                    <option value="">Select Role</option>
                    <option value="ProductionManager">Production Manager</option>
                    <option value="QualityController">Quality Controller</option>
                    <option value="InventoryClerk">Inventory Clerk</option>
                    <option value="Supervisor">Supervisor</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>Initial Status</label>
                <select name="status" class="form-control">
                    <option value="Pending">Pending</option>
                    <option value="Active">Active</option>
                    <option value="Inactive">Inactive</option>
                </select>
            </div>
        </div>
        
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <span class="material-icons">person_add</span>
                Add Employee
            </button>
            <a href="manage_employees.php" class="btn btn-secondary">
                <span class="material-icons">cancel</span>
                Cancel
            </a>
        </div>
    </form>
</div>

<script>
// Password match validation
const password = document.getElementById('password');
const confirmPassword = document.getElementById('confirm_password');
const passwordMsg = document.getElementById('password_match_msg');

function validatePassword() {
    if (password.value !== confirmPassword.value) {
        passwordMsg.innerHTML = 'Passwords do not match';
        passwordMsg.style.color = '#c62828';
        return false;
    } else if (confirmPassword.value.length > 0) {
        passwordMsg.innerHTML = 'Passwords match';
        passwordMsg.style.color = '#2e7d32';
        return true;
    }
    return true;
}

password.addEventListener('keyup', validatePassword);
confirmPassword.addEventListener('keyup', validatePassword);
</script>

<style>
.form-container {
    max-width: 800px;
    margin: 0 auto;
    background: var(--bg-white);
    border-radius: 16px;
    padding: 32px;
    box-shadow: var(--box-shadow);
}
.form-row.two-columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 20px;
}
.required {
    color: #c62828;
}
.form-actions {
    display: flex;
    gap: 12px;
    margin-top: 28px;
    padding-top: 20px;
    border-top: 1px solid #e0e0e0;
}
small {
    display: block;
    margin-top: 4px;
    font-size: 11px;
    color: #6c757d;
}
@media (max-width: 640px) {
    .form-row.two-columns {
        grid-template-columns: 1fr;
        gap: 16px;
    }
}
</style>

<?php include "../includes/footer.php"; ?>