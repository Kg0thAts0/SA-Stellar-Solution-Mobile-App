<?php
/**
 * Pagination Component
 * Usage: include this file after your query
 */

function renderPagination($current_page, $total_pages, $base_url) {
    if ($total_pages <= 1) return '';
    
    $html = '<div class="pagination">';
    
    // Previous button
    if ($current_page > 1) {
        $html .= '<a href="' . $base_url . '&page=' . ($current_page - 1) . '" class="page-link">&laquo; Previous</a>';
    } else {
        $html .= '<span class="page-link disabled">&laquo; Previous</span>';
    }
    
    // Page numbers
    $start = max(1, $current_page - 2);
    $end = min($total_pages, $current_page + 2);
    
    if ($start > 1) {
        $html .= '<a href="' . $base_url . '&page=1" class="page-link">1</a>';
        if ($start > 2) $html .= '<span class="page-dots">...</span>';
    }
    
    for ($i = $start; $i <= $end; $i++) {
        if ($i == $current_page) {
            $html .= '<span class="page-link active">' . $i . '</span>';
        } else {
            $html .= '<a href="' . $base_url . '&page=' . $i . '" class="page-link">' . $i . '</a>';
        }
    }
    
    if ($end < $total_pages) {
        if ($end < $total_pages - 1) $html .= '<span class="page-dots">...</span>';
        $html .= '<a href="' . $base_url . '&page=' . $total_pages . '" class="page-link">' . $total_pages . '</a>';
    }
    
    // Next button
    if ($current_page < $total_pages) {
        $html .= '<a href="' . $base_url . '&page=' . ($current_page + 1) . '" class="page-link">Next &raquo;</a>';
    } else {
        $html .= '<span class="page-link disabled">Next &raquo;</span>';
    }
    
    $html .= '</div>';
    return $html;
}

// Pagination CSS
echo '<style>
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-top: 20px;
    flex-wrap: wrap;
}
.page-link {
    display: inline-block;
    padding: 8px 14px;
    background: white;
    border: 1px solid #ddd;
    border-radius: 6px;
    color: #1a2634;
    text-decoration: none;
    font-size: 13px;
    transition: all 0.3s;
}
.page-link:hover {
    background: #c9a03d;
    border-color: #c9a03d;
    color: white;
}
.page-link.active {
    background: #1a2634;
    border-color: #1a2634;
    color: white;
}
.page-link.disabled {
    color: #ccc;
    pointer-events: none;
}
.page-dots {
    padding: 8px 4px;
    color: #6c757d;
}
.records-info {
    text-align: center;
    font-size: 12px;
    color: #6c757d;
    margin-top: 10px;
}
.per-page-selector {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-left: auto;
}
.per-page-selector select {
    padding: 6px;
    border-radius: 6px;
    border: 1px solid #ddd;
}
</style>';
?>