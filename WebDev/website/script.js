// Add all JavaScript from your original <script> tag here
// This will be shared across all pages

// Remove the showPage function since we'll use separate pages
// Keep only the animation and form submission code
document.addEventListener('DOMContentLoaded', function() {
    // Animation on scroll code
    const animateElements = document.querySelectorAll('.animate');
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = 1;
                entry.target.style.visibility = 'visible';
            }
        });
    }, { threshold: 0.1 });
    
    animateElements.forEach(element => {
        element.style.opacity = 0;
        element.style.visibility = 'hidden';
        element.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
        observer.observe(element);
    });
    
    // Form submission
    const contactForm = document.getElementById('contactForm');
    if(contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();
            alert('Thank you for your message! We will contact you shortly.');
            this.reset();
        });
    }
});