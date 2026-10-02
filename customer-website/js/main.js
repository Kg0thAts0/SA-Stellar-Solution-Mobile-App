// ============================================
// QA CLOTHING FACTORY - MAIN JAVASCRIPT
// Mobile Menu | Form Validation | Scroll Effects | Product Gallery
// ============================================

// Wait for DOM to load
document.addEventListener('DOMContentLoaded', function() {
    
    // ========== MOBILE MENU ==========
    const mobileBtn = document.querySelector('.mobile-menu-btn');
    const navigation = document.querySelector('.navigation');
    
    if (mobileBtn && navigation) {
        mobileBtn.addEventListener('click', function() {
            navigation.classList.toggle('active');
            
            // Change icon
            const icon = mobileBtn.querySelector('i');
            if (navigation.classList.contains('active')) {
                icon.classList.remove('fa-bars');
                icon.classList.add('fa-times');
            } else {
                icon.classList.remove('fa-times');
                icon.classList.add('fa-bars');
            }
        });
        
        // Close menu when clicking a link
        const navLinks = navigation.querySelectorAll('a');
        navLinks.forEach(link => {
            link.addEventListener('click', function() {
                navigation.classList.remove('active');
                const icon = mobileBtn.querySelector('i');
                icon.classList.remove('fa-times');
                icon.classList.add('fa-bars');
            });
        });
    }
    
    // ========== SCROLL ANIMATION (Fade In) ==========
    const fadeElements = document.querySelectorAll('.service-card, .testimonial-card, .badge, .step');
    
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);
    
    fadeElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'all 0.6s ease-out';
        observer.observe(el);
    });
    
    // ========== STATIC COUNTER (No animation, just display) ==========
    // Numbers are displayed directly - no counter animation needed for professional look
    
    // ========== FORM VALIDATION (For contact/quote pages) ==========
    // Will be used on contact.html and quote forms
    
    window.validateContactForm = function(formId) {
        const form = document.getElementById(formId);
        if (!form) return true;
        
        const inputs = form.querySelectorAll('input[required], textarea[required], select[required]');
        let isValid = true;
        
        inputs.forEach(input => {
            if (!input.value.trim()) {
                isValid = false;
                input.classList.add('error');
                
                // Create error message if not exists
                let errorMsg = input.parentElement.querySelector('.error-message');
                if (!errorMsg) {
                    errorMsg = document.createElement('small');
                    errorMsg.className = 'error-message';
                    errorMsg.style.color = '#E74C3C';
                    errorMsg.style.fontSize = '0.75rem';
                    errorMsg.style.marginTop = '0.25rem';
                    errorMsg.style.display = 'block';
                    input.parentElement.appendChild(errorMsg);
                }
                errorMsg.textContent = 'This field is required';
            } else {
                input.classList.remove('error');
                const errorMsg = input.parentElement.querySelector('.error-message');
                if (errorMsg) errorMsg.remove();
            }
            
            // Email validation
            if (input.type === 'email' && input.value.trim()) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(input.value.trim())) {
                    isValid = false;
                    input.classList.add('error');
                    let errorMsg = input.parentElement.querySelector('.error-message');
                    if (!errorMsg) {
                        errorMsg = document.createElement('small');
                        errorMsg.className = 'error-message';
                        errorMsg.style.color = '#E74C3C';
                        errorMsg.style.fontSize = '0.75rem';
                        input.parentElement.appendChild(errorMsg);
                    }
                    errorMsg.textContent = 'Please enter a valid email address';
                }
            }
            
            // Phone validation (South African)
            if (input.type === 'tel' && input.value.trim()) {
                const phoneRegex = /^[0-9\s\-\(\)\+]{10,}$/;
                if (!phoneRegex.test(input.value.trim())) {
                    isValid = false;
                    input.classList.add('error');
                    let errorMsg = input.parentElement.querySelector('.error-message');
                    if (!errorMsg) {
                        errorMsg = document.createElement('small');
                        errorMsg.className = 'error-message';
                        input.parentElement.appendChild(errorMsg);
                    }
                    errorMsg.textContent = 'Please enter a valid phone number';
                }
            }
        });
        
        return isValid;
    };
    
    // ========== HONEYPOT SPAM PROTECTION ==========
    const setupHoneypot = function(formId) {
        const form = document.getElementById(formId);
        if (!form) return;
        
        const honeypot = document.createElement('input');
        honeypot.type = 'text';
        honeypot.name = 'website';
        honeypot.style.display = 'none';
        honeypot.setAttribute('autocomplete', 'off');
        form.appendChild(honeypot);
        
        form.addEventListener('submit', function(e) {
            if (honeypot.value !== '') {
                e.preventDefault();
                console.log('Spam detected');
                return false;
            }
        });
    };
    
    // Apply honeypot to forms if they exist
    if (document.getElementById('contactForm')) setupHoneypot('contactForm');
    if (document.getElementById('quoteForm')) setupHoneypot('quoteForm');
    
    // ========== SMOOTH SCROLL FOR ANCHOR LINKS ==========
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                e.preventDefault();
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // ========== ADD ACTIVE CLASS TO CURRENT NAV ==========
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    const navLinks = document.querySelectorAll('.navigation a');
    
    navLinks.forEach(link => {
        const linkPage = link.getAttribute('href');
        if (linkPage === currentPage) {
            link.classList.add('active');
        } else if (currentPage === 'index.html' && linkPage === 'index.html') {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
    
    // ========== HEADER SCROLL EFFECT ==========
    const header = document.querySelector('.header');
    let lastScroll = 0;
    
    window.addEventListener('scroll', function() {
        const currentScroll = window.pageYOffset;
        
        if (currentScroll > 100) {
            header.style.boxShadow = '0 8px 24px rgba(0,0,0,0.12)';
            header.style.background = 'rgba(255,255,255,0.98)';
            header.style.backdropFilter = 'blur(10px)';
        } else {
            header.style.boxShadow = '0 2px 8px rgba(0,0,0,0.08)';
            header.style.background = '#FFFFFF';
            header.style.backdropFilter = 'none';
        }
        
        lastScroll = currentScroll;
    });
    
    // ============================================
    // PRODUCT GALLERY - LIGHTBOX FUNCTIONALITY
    // ============================================
    
    // Product Gallery Data
    const galleryData = {
        // School Wear
        'skirt': {
            title: 'School Skirts',
            description: 'Pleated and A-line styles in various school colors',
            images: [
                'images/products/Skirts1.jfif',
                'images/products/Skirts2.jfif',
                'images/products/Skirts3.jfif',
                'images/products/Skirts4.jfif'
            ]
        },
        'dress': {
            title: 'School Dresses',
            description: 'Comfortable fit with durable fabric for active students',
            images: [
                'images/products/Dresses2.jfif',
                'images/products/Dresses3.jfif',
                'images/products/Dresses4.jfif',
                'images/products/Dresses5.jfif'
            ]
        },
        'pinnafore': {
            title: 'Pinnafores',
            description: 'Classic design with adjustable straps for perfect fit',
            images: [
                'images/products/Pinnafores2.jfif',
                'images/products/Pinnafores3.jfif',
                'images/products/Pinnafores4.jfif',
                'images/products/Pinnafores5.jfif'
            ]
        },
        'shorts': {
            title: 'Shorts & Skorts',
            description: 'Perfect for sports and everyday school activities',
            images: [
                'images/products/Shorts & Skorts1.jfif',
                'images/products/Shorts & Skorts2.jfif',
                'images/products/Shorts & Skorts3.jfif',
                'images/products/Shorts & Skorts4.jfif'
            ]
        },
        'sports-shirt': {
            title: 'Vests & Sports Shirts',
            description: 'Moisture-wicking fabric for PE and sports activities',
            images: [
                'images/products/Vests & Sports Shirts2.jfif',
                'images/products/Vests & Sports Shirts3.jfif',
                'images/products/Vests & Sports Shirts4.jfif',
                'images/products/Vests & Sports Shirts5.jfif'
            ]
        },
        'tshirt': {
            title: 'T-Shirts & Golf Shirts',
            description: 'Comfortable everyday wear with school branding options',
            images: [
                'images/products/T-Shirts & Golf Shirts2.jfif',
                'images/products/T-Shirts & Golf Shirts3.jfif',
                'images/products/T-Shirts & Golf Shirts4.jfif',
                'images/products/T-Shirts & Golf Shirts5.jfif'
            ]
        },
        'trousers': {
            title: 'School Trousers',
            description: 'Reinforced knees and durable fabric for long-lasting wear',
            images: [
                'images/products/Trousers2.jfif',
                'images/products/Trousers3.jfif',
                'images/products/Trousers4.jfif',
                'images/products/Trousers5.jfif'
            ]
        },
        'tracksuit': {
            title: 'School Tracksuits',
            description: 'Warm and comfortable for winter sports and activities',
            images: [
                'images/products/Tracksuits2.jfif',
                'images/products/Tracksuits3.jfif',
                'images/products/Tracksuits4.jfif',
                'images/products/Tracksuits5.jfif'
            ]
        },
        // Corporate Wear
        'golf-shirt': {
            title: 'Corporate Golf Shirts',
            description: 'High-quality pique fabric with embroidered logo options',
            images: [
                'images/products/Golf Shirts2.jfif',
                'images/products/Golf Shirts3.jfif',
                'images/products/Golf Shirts4.jfif',
                'images/products/Golf Shirts5.jfif'
            ]
        },
        'lounge-shirt': {
            title: 'Lounge Shirts',
            description: 'Smart-casual style for a modern corporate look',
            images: [
                'images/products/Lounge Shirts2.jfif',
                'images/products/Lounge Shirts3.jfif',
                'images/products/Lounge Shirts4.jfif',
                'images/products/Lounge Shirts5.jfif'
            ]
        },
        'corporate-trousers': {
            title: 'Corporate Trousers',
            description: 'Professional trousers with a perfect, modern fit',
            images: [
                'images/products/CTrousers2.jpg',
                'images/products/CTrousers3.jfif',
                'images/products/CTrousers4.jfif',
                'images/products/CTrousers5.jfif'
            ]
        },
        'jacket': {
            title: 'Corporate Jackets',
            description: 'Blazers and jackets for formal corporate settings',
            images: [
                'images/products/Jackets2.jfif',
                'images/products/Jackets3.jfif',
                'images/products/Jackets4.jfif',
                'images/products/Jackets5.jfif'
            ]
        },
        'body-warmer': {
            title: 'Body Warmers',
            description: 'Lightweight warmth for outdoor corporate events',
            images: [
                'images/products/Body Warmers2.jfif',
                'images/products/Body Warmers3.jfif',
                'images/products/Body Warmers4.jfif',
                'images/products/Body Warmers5.jfif'
            ]
        },
        'executive': {
            title: 'Executive Wear',
            description: 'Premium tailored suits and executive formal wear',
            images: [
                'images/products/Executive Wear2.jfif',
                'images/products/Executive Wear3.jfif',
                'images/products/Executive Wear4.jfif',
                'images/products/Executive Wear5.jfif'
            ]
        },
        // PPE & Workwear
        'dustcoat': {
            title: 'Dustcoats',
            description: 'Lightweight protection for industrial and lab environments',
            images: [
                'images/products/Dustcoats2.jfif',
                'images/products/Dustcoats3.jfif',
                'images/products/Dustcoats4.jfif',
                'images/products/Dustcoats5.jfif'
            ]
        },
        'overall': {
            title: 'Overalls & Boilersuits',
            description: 'Full-body protection for industrial and mechanical work',
            images: [
                'images/products/Overalls & Boilersuits2.jfif',
                'images/products/Overalls & Boilersuits3.jfif',
                'images/products/Overalls & Boilersuits4.jfif',
                'images/products/Overalls & Boilersuits5.jfif'
            ]
        },
        'reflective': {
            title: 'Reflective Vests & Bibs',
            description: 'High-visibility gear with reflective strips for safety',
            images: [
                'images/products/Reflective Vests & Bibs2.jfif',
                'images/products/Reflective Vests & Bibs3.jfif',
                'images/products/Reflective Vests & Bibs4.jfif',
                'images/products/Reflective Vests & Bibs5.jfif'
            ]
        },
        'chef': {
            title: 'Chef Wear & Aprons',
            description: 'Stain-resistant, heat-protective kitchen uniforms',
            images: [
                'images/products/Chef Wear & Aprons2.jfif',
                'images/products/Chef Wear & Aprons3.jfif',
                'images/products/Chef Wear & Aprons4.jfif',
                'images/products/Chef Wear & Aprons5.jfif'
            ]
        },
        'hospital': {
            title: 'Hospital Clothing',
            description: 'Scrubs, gowns, and medical staff uniforms',
            images: [
                'images/products/Hospital Clothing2.jfif',
                'images/products/Hospital Clothing3.jfif',
                'images/products/Hospital Clothing4.jfif',
                'images/products/Hospital Clothing5.jfif'
            ]
        },
        'security': {
            title: 'Military & Security Uniforms',
            description: 'Durable, functional uniforms for security personnel',
            images: [
                'images/products/Military & Security Uniforms2.jfif',
                'images/products/Military & Security Uniforms2.jpg',
                'images/products/Military & Security Uniforms4.jfif',
                'images/products/Military & Security Uniforms5.jfif'
            ]
        },
        // Sports Wear
        'rugby': {
            title: 'Rugby Jerseys',
            description: 'Durable jerseys with reinforced stitching for rugby',
            images: [
                'images/products/Rugby Jerseys2.jfif',
                'images/products/Rugby Jerseys3.jfif',
                'images/products/Rugby Jerseys4.jfif',
                'images/products/Rugby Jerseys5.jfif'
            ]
        },
        'netball': {
            title: 'Netball Kits',
            description: 'Lightweight, breathable kits for netball teams',
            images: [
                'images/products/Netball Kits2.jfif',
                'images/products/Netball Kits3.png',
                'images/products/Netball Kits4.jfif',
                'images/products/Netball Kits5.jfif'
            ]
        },
        'soccer': {
            title: 'Soccer Uniforms',
            description: 'Professional soccer kits with moisture-wicking fabric',
            images: [
                'images/products/Soccer Uniforms2.jfif',
                'images/products/Soccer Uniforms3.jfif',
                'images/products/Soccer Uniforms4.jfif',
                'images/products/Soccer Uniforms5.jfif'
            ]
        },
        'athletic': {
            title: 'Athletic Kits',
            description: 'Lightweight running and track apparel',
            images: [
                'images/products/Athletic Kits2.jfif',
                'images/products/Athletic Kits3.jfif',
                'images/products/Athletic Kits4.jfif',
                'images/products/Athletic Kits5.jfif'
            ]
        },
        'sports-tracksuit': {
            title: 'Sports Tracksuits',
            description: 'Comfortable tracksuits for training and warm-ups',
            images: [
                'images/products/Tracksuit2.jfif',
                'images/products/Tracksuit3.jfif',
                'images/products/Tracksuit4.jfif',
                'images/products/Tracksuit5.jfif'
            ]
        },
        'bib': {
            title: 'Sports Bibs',
            description: 'Lightweight training bibs for team sports',
            images: [
                'images/products/Sports Bibs2.jfif',
                'images/products/Sports Bibs3.jfif',
                'images/products/Sports Bibs4.jfif',
                'images/products/Sports Bibs5.jfif'
            ]
        },
        // Casual Wear
        'casual-tshirt': {
            title: 'Casual T-Shirts',
            description: 'Soft, comfortable cotton t-shirts for everyday wear',
            images: [
                'images/products/Casual T-Shirts2.jfif',
                'images/products/Casual T-Shirts.jfif',
                'images/products/Casual T-Shirts3.jfif',
                'images/products/Casual T-Shirts5.jfif'
            ]
        },
        'hoodie': {
            title: 'Hoodies & Sweaters',
            description: 'Warm, stylish hoodies and sweaters for cold days',
            images: [
                'images/products/Hoodies & Sweaters2.jfif',
                'images/products/Hoodies & Sweaters3.jfif',
                'images/products/Hoodies & Sweaters4.jfif',
                'images/products/Hoodies & Sweaters5.jfif'
            ]
        },
        'cargo': {
            title: 'Cargo Pants & Shorts',
            description: 'Functional cargo pants with multiple pockets',
            images: [
                'images/products/Cargo Pants & Shorts2.jfif',
                'images/products/Cargo Pants & Shorts3.jfif',
                'images/products/Cargo Pants & Shorts4.jfif',
                'images/products/Cargo Pants & Shorts5.jfif'
            ]
        },
        'thermal': {
            title: 'Thermal Underwear',
            description: 'Insulating thermal wear for cold weather',
            images: [
                'images/products/Thermal Underwear.jfif',
                'images/products/Thermal Underwear2.jfif',
                'images/products/Thermal Underwear3.jfif',
                'images/products/Thermal Underwear5.jfif'
            ]
        },
        'hunting': {
            title: 'Hunting & Fishing Wear',
            description: 'Durable outdoor apparel for hunting and fishing',
            images: [
                'images/products/Hunting & Fishing Wear2.jfif',
                'images/products/Hunting & Fishing Wear3.jfif',
                'images/products/Hunting & Fishing Wear4.jfif',
                'images/products/Hunting & Fishing Wear5.jfif'
            ]
        },
        'custom': {
            title: 'Custom Apparel',
            description: 'Custom-designed apparel to your specifications',
            images: [
                'images/products/Custom Apparel2.jfif',
                'images/products/Custom Apparel3.jfif',
                'images/products/Custom Apparel4.jfif',
                'images/products/Custom Apparel5.jfif'
            ]
        }
    };

    // Lightbox elements
    const lightbox = document.getElementById('lightbox');
    const lightboxImg = document.getElementById('lightbox-img');
    const lightboxTitle = document.getElementById('lightbox-title');
    const lightboxDesc = document.getElementById('lightbox-desc');
    const lightboxCounter = document.getElementById('lightbox-counter');
    const thumbnailsContainer = document.getElementById('thumbnails');
    const closeBtn = document.querySelector('.close-lightbox');
    const prevBtn = document.querySelector('.prev-btn');
    const nextBtn = document.querySelector('.next-btn');

    let currentGallery = [];
    let currentIndex = 0;
    let currentGalleryId = '';

    // Only initialize gallery if we're on a page with products
    if (document.querySelector('.product-item')) {
        // Open lightbox when product is clicked
        document.querySelectorAll('.product-item').forEach(item => {
            item.addEventListener('click', function() {
                const galleryId = this.dataset.gallery;
                if (galleryData[galleryId]) {
                    openLightbox(galleryId);
                }
            });
        });

        function openLightbox(galleryId) {
            currentGalleryId = galleryId;
            const data = galleryData[galleryId];
            currentGallery = data.images;
            currentIndex = 0;
            
            lightboxTitle.textContent = data.title;
            lightboxDesc.textContent = data.description;
            
            updateLightbox();
            lightbox.classList.add('active');
            document.body.style.overflow = 'hidden';
        }

        function updateLightbox() {
            if (currentGallery.length > 0) {
                lightboxImg.src = currentGallery[currentIndex];
                lightboxCounter.textContent = `${currentIndex + 1} / ${currentGallery.length}`;
                
                // Update thumbnails
                thumbnailsContainer.innerHTML = '';
                currentGallery.forEach((img, index) => {
                    const thumb = document.createElement('img');
                    thumb.src = img;
                    thumb.alt = `Thumbnail ${index + 1}`;
                    thumb.classList.toggle('active-thumb', index === currentIndex);
                    thumb.addEventListener('click', () => {
                        currentIndex = index;
                        updateLightbox();
                    });
                    thumbnailsContainer.appendChild(thumb);
                });
                
                // Update button states
                if (prevBtn) prevBtn.disabled = currentIndex === 0;
                if (nextBtn) nextBtn.disabled = currentIndex === currentGallery.length - 1;
            }
        }

        // Navigation functions
        function prevImage() {
            if (currentIndex > 0) {
                currentIndex--;
                updateLightbox();
            }
        }

        function nextImage() {
            if (currentIndex < currentGallery.length - 1) {
                currentIndex++;
                updateLightbox();
            }
        }

        // Event listeners for navigation
        if (prevBtn) prevBtn.addEventListener('click', prevImage);
        if (nextBtn) nextBtn.addEventListener('click', nextImage);

        // Keyboard navigation
        document.addEventListener('keydown', (e) => {
            if (!lightbox || !lightbox.classList.contains('active')) return;
            
            if (e.key === 'Escape') closeLightbox();
            if (e.key === 'ArrowLeft') prevImage();
            if (e.key === 'ArrowRight') nextImage();
        });

        // Close lightbox
        function closeLightbox() {
            if (lightbox) {
                lightbox.classList.remove('active');
                document.body.style.overflow = '';
            }
        }

        if (closeBtn) closeBtn.addEventListener('click', closeLightbox);
        if (lightbox) {
            lightbox.addEventListener('click', (e) => {
                if (e.target === lightbox) closeLightbox();
            });
        }

        // Touch support for swipe
        let touchStartX = 0;
        let touchEndX = 0;

        if (lightbox) {
            lightbox.addEventListener('touchstart', (e) => {
                touchStartX = e.changedTouches[0].screenX;
            });

            lightbox.addEventListener('touchend', (e) => {
                touchEndX = e.changedTouches[0].screenX;
                handleSwipe();
            });
        }

        function handleSwipe() {
            const swipeThreshold = 50;
            const diff = touchStartX - touchEndX;
            
            if (Math.abs(diff) > swipeThreshold) {
                if (diff > 0) {
                    nextImage();
                } else {
                    prevImage();
                }
            }
        }
    }

    console.log('QA Clothing Factory - Website Loaded');
});