// Portfolio Items
const portfolioItems = [
    { 
        image: "",
        title: "Gothic Angel Backpiece",
        year: "2023" 
    },
    { 
        image: "",
        title: "Mythological Sleeve",
        year: "2022" 
    },
    { 
        image: "",
        title: "Dark Botanical Forearm",
        year: "2023" 
    },
    { 
        image: "",
        title: "Occult Hand Design",
        year: "2021" 
    },
    { 
        image: "",
        title: "Full Back Illustration",
        year: "2023" 
    },
    { 
        image: "",
        title: "Rib Cage Composition",
        year: "2022" 
    }
];

// Populate Portfolio
const portfolioContainer = document.getElementById('portfolioContainer');

portfolioItems.forEach(item => {
    const portfolioItem = document.createElement('div');
    portfolioItem.className = 'portfolio-item';
    
    portfolioItem.innerHTML = `
        <img src="${item.image}" alt="${item.title}">
        <div class="portfolio-overlay">
            <h3>${item.title}</h3>
            <p>${item.year}</p>
        </div>
    `;
    
    portfolioContainer.appendChild(portfolioItem);
});

// FAQ Accordion
const faqItems = document.querySelectorAll('.faq-item');

faqItems.forEach(item => {
    const question = item.querySelector('h3');
    
    question.addEventListener('click', () => {
        item.classList.toggle('active');
        
        // Close other open items
        faqItems.forEach(otherItem => {
            if (otherItem !== item && otherItem.classList.contains('active')) {
                otherItem.classList.remove('active');
            }
        });
    });
});

// Booking Form
document.getElementById('bookingForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const formData = {
        name: this.name.value,
        email: this.email.value,
        idea: this.idea.value,
        placement: this.placement.value
    };
    
    // In a real site, you would send this to a server
    console.log('Booking Request:', formData);
    
    alert(`Thank you ${formData.name}! Your deposit request has been sent. Jacob will contact you within 7 days to discuss your ${formData.placement} tattoo idea.`);
    
    this.reset();
});

// Add current year to footer
document.querySelector('.copyright p').innerHTML = 
    `&copy; ${new Date().getFullYear()} Jacob Tattoos. All rights reserved.`;
