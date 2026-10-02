<?php
// Start session if not already started
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Helper function for safe output (if not defined)
if (!function_exists('h')) {
    function h($str) {
        return htmlspecialchars($str ?? '', ENT_QUOTES, 'UTF-8');
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../css/style.css">
    <title>QA Clothing Factory System</title>
</head>
<body>

<!-- Top Navigation Bar -->
<nav class="top-nav">
    <div class="nav-container">
        <div class="nav-brand">
            <span class="brand-name">QA Clothing Factory System</span>
        </div>
        
        <div class="nav-user">
            <!-- User Info -->
            <span class="user-name"><?php echo h($_SESSION['full_name'] ?? 'Guest'); ?></span>
            <span class="user-role"><?php echo h($_SESSION['role'] ?? ''); ?></span>
            
            <!-- Notification Bell -->
            <div class="notification-dropdown" id="notificationDropdown">
                <button class="notification-btn" onclick="toggleNotifications()">
                    <span class="material-icons">notifications</span>
                    <span class="notification-badge" id="notificationBadge" style="display:none;">0</span>
                </button>
                <div class="notification-menu" id="notificationMenu">
                    <div class="notification-header">
                        <h4>Notifications</h4>
                        <button onclick="markAllRead()">Mark all read</button>
                    </div>
                    <div class="notification-list" id="notificationList">
                        <div class="notification-loading">Loading...</div>
                    </div>
                </div>
            </div>
            <!-- Dark Mode Toggle -->
            <button class="dark-mode-toggle" id="darkModeToggle" title="Dark Mode">
                <span class="material-icons">dark_mode</span>
            </button>
            
            <!-- Profile Link -->
            <a href="../profile.php" class="profile-link" title="My Profile">
                <span class="material-icons">account_circle</span>
            </a>
            
            <!-- Logout Form -->
            <form id="logoutForm" method="POST" action="../logout.php" style="display: none;">
                <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token'] ?? ''; ?>">
            </form>
            
            <!-- Logout Button -->
            <button class="logout-btn" onclick="confirmLogout()" title="Logout">
                <span class="material-icons">logout</span>
            </button>
        </div>
    </div>
</nav>

<!-- Sidebar Toggle Button (Mobile) -->
<button class="sidebar-toggle" id="sidebarToggle">
    <span class="material-icons">menu</span>
</button>

<script>
// Notification functions
function loadNotifications() {
    fetch('../api/notifications.php')
        .then(response => response.json())
        .then(data => {
            const badge = document.getElementById('notificationBadge');
            const list = document.getElementById('notificationList');
            
            if (data && data.length > 0) {
                badge.style.display = 'flex';
                badge.textContent = data.length;
                
                list.innerHTML = data.map(n => `
                    <a href="${n.link}" class="notification-item notification-${n.type}">
                        <span class="material-icons">${n.icon}</span>
                        <div class="notification-content">
                            <p>${n.message}</p>
                        </div>
                    </a>
                `).join('');
            } else {
                badge.style.display = 'none';
                list.innerHTML = '<div class="notification-empty">No new notifications</div>';
            }
        })
        .catch(error => {
            console.error('Error loading notifications:', error);
            document.getElementById('notificationList').innerHTML = '<div class="notification-empty">Unable to load notifications</div>';
        });
}

function toggleNotifications() {
    const menu = document.getElementById('notificationMenu');
    menu.classList.toggle('show');
}

function markAllRead() {
    document.getElementById('notificationBadge').style.display = 'none';
    document.getElementById('notificationMenu').classList.remove('show');
}

function confirmLogout() {
    if (confirm('Are you sure you want to logout?')) {
        document.getElementById('logoutForm').submit();
    }
}

// Close notification menu when clicking outside
document.addEventListener('click', function(e) {
    const dropdown = document.getElementById('notificationDropdown');
    if (dropdown && !dropdown.contains(e.target)) {
        const menu = document.getElementById('notificationMenu');
        if (menu) menu.classList.remove('show');
    }
});

// Load notifications every 30 seconds
if (document.getElementById('notificationDropdown')) {
    loadNotifications();
    setInterval(loadNotifications, 30000);
}

// Sidebar toggle
document.getElementById('sidebarToggle')?.addEventListener('click', function() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.toggle('collapsed');
        document.body.classList.toggle('sidebar-collapsed');
    }
});
</script>

<style>
/* Top Navigation */
.top-nav {
    background: #1a2634;
    color: white;
    padding: 12px 24px;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.nav-brand .brand-name {
    font-size: 18px;
    font-weight: 600;
}

.nav-user {
    display: flex;
    align-items: center;
    gap: 16px;
}

.user-name {
    font-size: 14px;
    font-weight: 500;
}

.user-role {
    font-size: 12px;
    background: rgba(255,255,255,0.2);
    padding: 4px 10px;
    border-radius: 20px;
}

.profile-link {
    color: white;
    text-decoration: none;
    display: flex;
    align-items: center;
}

.profile-link .material-icons {
    font-size: 24px;
}

.logout-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 6px;
    color: white;
    display: flex;
    align-items: center;
}

.logout-btn:hover {
    background: rgba(255,255,255,0.1);
}

/* Notification Styles */
.notification-dropdown {
    position: relative;
}

.notification-btn {
    background: none;
    border: none;
    cursor: pointer;
    position: relative;
    padding: 4px 8px;
    border-radius: 6px;
    color: white;
    display: flex;
    align-items: center;
}

.notification-btn:hover {
    background: rgba(255,255,255,0.1);
}

.notification-btn .material-icons {
    font-size: 22px;
}

.notification-badge {
    position: absolute;
    top: -2px;
    right: -2px;
    background: #c62828;
    color: white;
    font-size: 10px;
    font-weight: bold;
    border-radius: 50%;
    width: 18px;
    height: 18px;
    display: none;
    align-items: center;
    justify-content: center;
}

.notification-menu {
    position: absolute;
    top: 40px;
    right: 0;
    width: 340px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    display: none;
    z-index: 1001;
    overflow: hidden;
}

.notification-menu.show {
    display: block;
}

.notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: #f8f9fa;
    border-bottom: 1px solid #e0e0e0;
}

.notification-header h4 {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    color: #1a2634;
}

.notification-header button {
    background: none;
    border: none;
    font-size: 11px;
    color: #c9a03d;
    cursor: pointer;
}

.notification-list {
    max-height: 380px;
    overflow-y: auto;
}

.notification-item {
    display: flex;
    gap: 12px;
    padding: 12px 16px;
    text-decoration: none;
    border-bottom: 1px solid #f0f0f0;
    transition: background 0.3s;
}

.notification-item:hover {
    background: #f8f9fa;
}

.notification-item .material-icons {
    font-size: 20px;
}

.notification-warning .material-icons {
    color: #ed6c02;
}

.notification-info .material-icons {
    color: #0288d1;
}

.notification-success .material-icons {
    color: #2e7d32;
}

.notification-content p {
    margin: 0;
    font-size: 13px;
    color: #2c3e50;
    line-height: 1.4;
}

.notification-loading,
.notification-empty {
    padding: 24px;
    text-align: center;
    color: #6c757d;
    font-size: 13px;
}

/* Sidebar Toggle Button (Mobile) */
.sidebar-toggle {
    display: none;
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 1001;
    background: #c9a03d;
    border: none;
    width: 48px;
    height: 48px;
    border-radius: 50%;
    box-shadow: 0 2px 10px rgba(0,0,0,0.2);
    cursor: pointer;
    color: white;
}

.sidebar-toggle .material-icons {
    font-size: 24px;
}

@media (max-width: 768px) {
    .sidebar-toggle {
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .user-name, .user-role {
        display: none;
    }
}
</style>