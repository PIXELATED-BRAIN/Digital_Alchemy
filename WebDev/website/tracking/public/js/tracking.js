document.addEventListener('DOMContentLoaded', () => {
    const trackBtn = document.getElementById('track-btn');
    const trackingResult = document.getElementById('tracking-result');
    
    trackBtn.addEventListener('click', trackPackage);
    
    async function trackPackage() {
        const trackingNumber = document.getElementById('tracking-number').value;
        
        if (!trackingNumber) {
            showNotification('Please enter a tracking number', 'error');
            return;
        }
        
        try {
            const response = await fetch(`/api/track/${trackingNumber}`);
            const data = await response.json();
            
            if (data.error) {
                showNotification(data.error, 'error');
                trackingResult.style.display = 'none';
                return;
            }
            
            renderTrackingResult(data);
            trackingResult.style.display = 'block';
            showNotification('Tracking information retrieved', 'success');
            
            // Scroll to results
            trackingResult.scrollIntoView({ behavior: 'smooth', block: 'start' });
        } catch (error) {
            showNotification('Failed to fetch tracking information', 'error');
            console.error('Error:', error);
        }
    }
    
    function renderTrackingResult(data) {
        const statusClassMap = {
            'Processing': 'status-processing',
            'In Transit': 'status-transit',
            'Out for Delivery': 'status-out',
            'Delivered': 'status-delivered'
        };
        
        const statusTextMap = {
            'Processing': 'Order Processed',
            'In Transit': 'In Transit',
            'Out for Delivery': 'Out for Delivery',
            'Delivered': 'Delivered'
        };
        
        const timelineSteps = [
            { 
                status: 'Processing', 
                title: 'Order Processed', 
                content: 'Package is being prepared for shipment at our facility',
                active: data.status === 'Processing',
                completed: ['Processing', 'In Transit', 'Out for Delivery', 'Delivered'].includes(data.status)
            },
            { 
                status: 'In Transit', 
                title: 'In Transit', 
                content: 'Package is on route to destination',
                active: data.status === 'In Transit',
                completed: ['In Transit', 'Out for Delivery', 'Delivered'].includes(data.status)
            },
            { 
                status: 'Out for Delivery', 
                title: 'Out for Delivery', 
                content: 'Package will be delivered to your address today',
                active: data.status === 'Out for Delivery',
                completed: ['Out for Delivery', 'Delivered'].includes(data.status)
            },
            { 
                status: 'Delivered', 
                title: 'Delivered', 
                content: 'Package has been delivered to recipient',
                active: data.status === 'Delivered',
                completed: data.status === 'Delivered'
            }
        ];
        
        const timelineHTML = timelineSteps.map(step => `
            <div class="timeline-step ${step.completed ? 'completed' : ''} ${step.active ? 'active' : ''}">
                <div class="timeline-icon"></div>
                <div class="timeline-date">${step.active ? new Date().toLocaleString() : 'Expected soon'}</div>
                <div class="timeline-title">${step.title}</div>
                <div class="timeline-content">${step.content}</div>
            </div>
        `).join('');
        
        trackingResult.innerHTML = `
            <div class="tracking-header">
                <div class="tracking-number">
                    <i class="fas fa-barcode"></i> ${data.tracking_number}
                </div>
                <div class="status-badge ${statusClassMap[data.status]}">${data.status}</div>
            </div>
            
            <div class="delivery-info-grid">
                <div class="info-card">
                    <h4>RECIPIENT</h4>
                    <p>${data.recipient_name || 'Not specified'}</p>
                </div>
                <div class="info-card">
                    <h4>ORIGIN</h4>
                    <p>${data.origin || 'Main Warehouse'}</p>
                </div>
                <div class="info-card">
                    <h4>DESTINATION</h4>
                    <p>${data.destination || 'Unknown'}</p>
                </div>
                <div class="info-card">
                    <h4>ESTIMATED DELIVERY</h4>
                    <p>${data.estimated_delivery || 'Not available'}</p>
                </div>
            </div>
            
            <h3 style="margin: 25px 0 15px; display: flex; align-items: center; gap: 10px;">
                <i class="fas fa-map-marker-alt"></i> Delivery Progress
            </h3>
            <div class="timeline">
                ${timelineHTML}
            </div>
        `;
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
    
    // Initialize with a sample tracking number
    document.getElementById('tracking-number').value = 'AMA-######';
}); 