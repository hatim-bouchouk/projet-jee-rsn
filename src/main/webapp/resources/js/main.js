/**
 * Main JavaScript file for the Supply Chain Management System
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('Supply Chain Management System initialized');
    
    // Add event listeners for interactive elements
    setupNavigation();
    setupForms();
    
    // Initialize any dashboard widgets that need JavaScript
    initializeDashboard();
});

/**
 * Set up responsive navigation
 */
function setupNavigation() {
    // This will be expanded later for mobile menu toggle and active page highlighting
    const navLinks = document.querySelectorAll('nav a');
    
    // Highlight the current page in navigation
    navLinks.forEach(link => {
        if (link.href === window.location.href) {
            link.classList.add('active');
        }
    });
}

/**
 * Set up form validation and submission
 */
function setupForms() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            // Form validation will be added here
            // For now, just log the submission
            console.log('Form submitted:', this.id || 'unnamed form');
        });
    });
}

/**
 * Initialize dashboard widgets 
 */
function initializeDashboard() {
    // This will be expanded as dashboard widgets are implemented
    const widgets = document.querySelectorAll('.widget');
    
    if (widgets.length > 0) {
        console.log(`Initialized ${widgets.length} dashboard widgets`);
        
        // In the future, this might include AJAX calls to update widget data
    }
}

/**
 * Utility function for AJAX requests
 * @param {string} url - The URL to send the request to
 * @param {string} method - HTTP method (GET, POST, etc.)
 * @param {Object} data - Data to send with the request
 * @param {Function} callback - Function to call with the response
 */
function ajaxRequest(url, method, data, callback) {
    const xhr = new XMLHttpRequest();
    xhr.open(method, url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    
    xhr.onload = function() {
        if (xhr.status >= 200 && xhr.status < 300) {
            callback(null, JSON.parse(xhr.responseText));
        } else {
            callback(new Error(`Request failed with status ${xhr.status}`));
        }
    };
    
    xhr.onerror = function() {
        callback(new Error('Network error'));
    };
    
    xhr.send(data ? JSON.stringify(data) : null);
} 