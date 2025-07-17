document.addEventListener('DOMContentLoaded', () => {
    const adminLoginBtn = document.getElementById('admin-login-btn');
    const logoutBtn = document.getElementById('logout-btn');
    const createDeliveryBtn = document.getElementById('create-delivery-btn');
    const refreshBtn = document.getElementById('refresh-deliveries');
    const adminPanel = document.getElementById('admin-panel');
    const adminLoginSection = document.getElementById('admin-login-section');
    
    // Check if user is already logged in
    checkAuth();
    
    // Event listeners
    adminLoginBtn.addEventListener('click', adminLogin);
    logoutBtn.addEventListener('click', logout);
    createDeliveryBtn.addEventListener('click', createDelivery);
    refreshBtn.addEventListener('click', loadDeliveries);
    
    // Delegated event for update buttons
    document.getElementById('deliveries-container').addEventListener('click', (e) => {
        if (e.target.classList.contains('update-btn') || e.target.closest('.update-btn')) {
            updateDeliveryStatus(e.target.closest('.update-btn'));
        }
    });
    
    async function checkAuth() {
        try {
            const response = await fetch('/api/admin/check-auth');
            if (response.status === 200) {
                const data = await response.json();
                if (data.authenticated) {
                    showAdminPanel();
                }
            }
        } catch (error) {
            console.error('Auth check failed:', error);
        }
    }
    
    async function adminLogin() {
        const username = document.getElementById('admin-username').value;
        const password = document.getElementById('admin-password').value;
        
        if (!username || !password) {
            showNotification('Please enter both username and password', 'error');
            return;
        }
        
        try {
            const response = await fetch('/api/admin/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            
            const data = await response.json();
            
            if (data.error) {
                showNotification(data.error, 'error');
                return;
            }
            
            showAdminPanel();
            showNotification('Admin login successful', 'success');
        } catch (error) {
            showNotification('Login failed, please try again', 'error');
            console.error('Error:', error);
        }
    }
    
    function showAdminPanel() {
        adminLoginSection.style.display = 'none';
        adminPanel.style.display = 'block';
        loadDeliveries();
    }
    
    function showLogin() {
        adminLoginSection.style.display = 'block';
        adminPanel.style.display = 'none';
    }
    
    async function logout() {
        try {
            const response = await fetch('/api/admin/logout', { method: 'POST' });
            const data = await response.json();
            
            if (data.success) {
                showLogin();
                showNotification('You have been logged out', 'success');
            } else {
                showNotification('Logout failed, please try again', 'error');
            }
        } catch (error) {
            showNotification('Logout failed, please try again', 'error');
            console.error('Error:', error);
        }
    }
    
    async function createDelivery() {
        const recipientName = document.getElementById('recipient-name').value;
        const deliveryAddress = document.getElementById('delivery-address').value;
        const packageType = document.getElementById('package-type').value;
        
        if (!recipientName || !deliveryAddress) {
            showNotification('Please fill all required fields', 'error');
            return;
        }
        
        try {
            const response = await fetch('/api/admin/create-delivery', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    recipient_name: recipientName, 
                    delivery_address: deliveryAddress, 
                    package_type: packageType 
                })
            });
            
            const data = await response.json();
            
            if (data.error) {
                showNotification(data.error, 'error');
                return;
            }
            
            showNotification(`Delivery created! Tracking number: ${data.tracking_number}`, 'success');
            
            // Reset form
            document.getElementById('recipient-name').value = '';
            document.getElementById('delivery-address').value = '';
            document.getElementById('package-type').value = 'Document';
            
            // Refresh deliveries list
            loadDeliveries();
        } catch (error) {
            showNotification('Failed to create delivery', 'error');
            console.error('Error:', error);
        }
    }
    
    async function loadDeliveries() {
        try {
            const response = await fetch('/api/admin/deliveries');
            const deliveries = await response.json();
            
            if (response.status !== 200) {
                throw new Error(deliveries.error || 'Failed to load deliveries');
            }
            
            renderDeliveries(deliveries);
        } catch (error) {
            showNotification(error.message, 'error');
            console.error('Error:', error);
        }
    }
    
    function renderDeliveries(deliveries) {
        const container = document.getElementById('deliveries-container');
        container.innerHTML = '';
        
        if (deliveries.length === 0) {
            container.innerHTML = '<div class="empty-state"><i class="fas fa-box-open"></i><p>No deliveries found</p></div>';
            return;
        }
        
        const packageIcons = {
            'Document': 'file',
            'Parcel': 'box',
            'Box': 'box-open',
            'Pallet': 'pallet',
            'Fragile': 'gift'
        };
        
        deliveries.forEach(delivery => {
            const item = document.createElement('div');
            item.className = 'delivery-item';
            item.innerHTML = `
                <div>
                    <strong>${delivery.tracking_number}</strong>
                    <div style="font-size: 13px; color: var(--gray); margin-top: 5px;">
                        <i class="fas fa-${packageIcons[delivery.package_type] || 'box'}"></i> ${delivery.package_type}
                    </div>
                </div>
                <div>${delivery.recipient_name}</div>
                <div>
                    <select class="status-select" data-tracking="${delivery.tracking_number}">
                        <option value="Processing" ${delivery.status === 'Processing' ? 'selected' : ''}>Processing</option>
                        <option value="In Transit" ${delivery.status === 'In Transit' ? 'selected' : ''}>In Transit</option>
                        <option value="Out for Delivery" ${delivery.status === 'Out for Delivery' ? 'selected' : ''}>Out for Delivery</option>
                        <option value="Delivered" ${delivery.status === 'Delivered' ? 'selected' : ''}>Delivered</option>
                    </select>
                </div>
                <div class="admin-actions">
                    <button class="btn btn-warning update-btn" style="padding: 8px 12px;">
                        <i class="fas fa-save"></i>
                    </button>
                </div>
            `;
            container.appendChild(item);
        });
    }
    
    async function updateDeliveryStatus(button) {
        const item = button.closest('.delivery-item');
        const trackingNumber = item.querySelector('.status-select').dataset.tracking;
        const status = item.querySelector('.status-select').value;
        
        try {
            const response = await fetch('/api/admin/update-status', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ tracking_number: trackingNumber, status })
            });
            
            const data = await response.json();
            
            if (data.error) {
                showNotification(data.error, 'error');
                return;
            }
            
            showNotification(`Status updated for ${trackingNumber} to ${status}`, 'success');
        } catch (error) {
            showNotification('Failed to update status', 'error');
            console.error('Error:', error);
        }
    }
    
    function showNotification(message, type) {
        const notification = document.getElementById('notification');
        const icon = notification.querySelector('i');
        const content = notification.querySelector('.notification-content');
        
        // Set content and style
        content.textContent = message;
        notification.className = 'notification';
        
        if (type === 'success') {
            notification.classList.add('notification-success');
            icon.className = 'fas fa-check-circle';
        } else {
            notification.classList.add('notification-error');
            icon.className = 'fas fa-exclamation-circle';
        }
        
        // Show notification
        notification.classList.add('show');
        
        // Hide after 3 seconds
        setTimeout(() => {
            notification.classList.remove('show');
        }, 3000);
    }
});