<?php
/**
 * Main Layout Wrapper
 * Include this file after authentication in all dashboard pages
 */

require_once __DIR__ . '/auth.php';
?>
<div class="app-wrapper">
    <?php include __DIR__ . '/header.php'; ?>
    <?php include __DIR__ . '/sidebar.php'; ?>
    
    <main class="main-content">
        <div class="container-fluid">
            <!-- Flash Messages -->
            <?php if (isset($_SESSION['success'])): ?>
            <div class="alert alert-success">
                <span class="material-icons">check_circle</span>
                <span><?php echo h($_SESSION['success']); ?></span>
                <button class="alert-close" onclick="this.parentElement.remove();">&times;</button>
            </div>
            <?php unset($_SESSION['success']); endif; ?>
            
            <?php if (isset($_SESSION['error'])): ?>
            <div class="alert alert-error">
                <span class="material-icons">error</span>
                <span><?php echo h($_SESSION['error']); ?></span>
                <button class="alert-close" onclick="this.parentElement.remove();">&times;</button>
            </div>
            <?php unset($_SESSION['error']); endif; ?>
            
            <!-- Page Content Starts Here -->