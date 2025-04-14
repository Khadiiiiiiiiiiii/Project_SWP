<link rel="stylesheet" href="CSS/footerPage.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

<!-- Footer -->
<footer class="footer">
    <div class="footer-container">
        <div class="footer-column">
            <h3>Company</h3>
            <ul>
                <li><a href="About.jsp">About Us</a></li>
                <li><a href="Contact.jsp">Contact</a></li>
                <li><a href="#">Privacy Policy</a></li>
                <li><a href="#">Affiliate Program</a></li>
            </ul>
        </div>
        <div class="footer-column">
            <h3>Get Help</h3>
            <ul>
                <li><a href="#">FAQ</a></li>
                <li><a href="#">Shipping</a></li>
                <li><a href="#">Returns</a></li>
                <li><a href="historyOrder">Order Status</a></li>
                <li><a href="#">Payment Options</a></li>
            </ul>
        </div>
        <div class="footer-column">
            <h3>Online Shop</h3>
            <ul>
                <li><a href="category?category=1">Laptop</a></li>
                <li><a href="category?category=2">Mouse</a></li>
                <li><a href="category?category=3">Keyboard</a></li>
                <li><a href="category?category=4">Screen</a></li>
            </ul>
        </div>
        <div class="footer-column social-links">
            <h3>Follow Us</h3>
            <div class="social-icons">
                <a href="#" class="social-icon"><i class="fab fa-facebook-f"></i></a>
                <a href="#" class="social-icon"><i class="fab fa-twitter"></i></a>
                <a href="#" class="social-icon"><i class="fab fa-instagram"></i></a>
                <a href="#" class="social-icon"><i class="fab fa-linkedin-in"></i></a>
            </div>
        </div>
    </div>
    <div class="footer-bottom">
        <p>&copy; 2025 Your Company. All Rights Reserved.</p>
    </div>
</footer>

<!-- Back to Top Button -->
<a href="#" class="back-to-top" id="back-to-top">
    <i class="fas fa-arrow-up"></i>
</a>

<!-- JavaScript for Back to Top -->
<script>
    // Hi?n th? n t "Back to Top" khi cu?n xu?ng
    window.addEventListener('scroll', function () {
        const backToTop = document.getElementById('back-to-top');
        if (window.scrollY > 300) {
            backToTop.classList.add('visible');
        } else {
            backToTop.classList.remove('visible');
        }
    });

    // Cu?n m??t m  l n ??u trang khi nh?n n t
    document.getElementById('back-to-top').addEventListener('click', function (e) {
        e.preventDefault();
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
</script>