<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Contact</title>
        <!-- Thêm Font Awesome để sử dụng biểu tượng -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            /* CSS để làm footer dính xuống dưới */
            html, body {
                height: 100%;
                margin: 0;
                padding: 0;
            }
            body {
                font-family: Arial, sans-serif;
                background-color: #f5f5f5;
                display: flex;
                flex-direction: column;
                min-height: 100vh; /* Đảm bảo chiều cao tối thiểu là 100% viewport */
            }
            .container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 20px;
                flex: 1; /* Nội dung chính chiếm không gian linh hoạt, đẩy footer xuống dưới */
            }
            /* CSS cho phần liên hệ */
            .contact-info {
                background-color: #fff;
                padding: 30px;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                margin-bottom: 20px;
                text-align: center;
            }
            .contact-info h2 {
                color: #007bff;
                font-size: 1.8em;
                margin-bottom: 20px;
            }
            .contact-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 20px;
                margin-top: 20px;
            }
            .contact-item {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
                padding: 15px;
                background-color: #f9f9f9;
                border-radius: 8px;
                transition: transform 0.3s ease;
            }
            .contact-item:hover {
                transform: translateY(-5px);
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            }
            .contact-item i {
                font-size: 1.5em;
                color: #007bff;
            }
            .contact-item p {
                margin: 0;
                color: #333;
                font-size: 1em;
            }
            .contact-item a {
                color: #007bff;
                text-decoration: none;
            }
            .contact-item a:hover {
                text-decoration: underline;
            }
            /* Đảm bảo footer không bị ảnh hưởng bởi flex */
            footer {
                flex-shrink: 0; /* Footer không bị co lại */
            }
        </style>
    </head>
    <body>
        <!-- Nhúng navbar.jsp -->
        <jsp:include page="navbar.jsp" />

        <!-- Nội dung chính -->
        <div class="container">
            <!-- Phần thông tin liên hệ -->
            <div class="contact-info">
                <h2>Liên hệ với chúng tôi</h2>
                <div class="contact-grid">
                    <div class="contact-item">
                        <i class="fas fa-envelope"></i>
                        <p>Email: <a href="mailto:admin@example.com">admin@example.com</a></p>
                    </div>
                    <div class="contact-item">
                        <i class="fas fa-phone"></i>
                        <p>Hotline: <a href="tel:+842471234567">+84 123456789</a></p>
                    </div>
                    <div class="contact-item">
                        <i class="fas fa-map-marker-alt"></i>
                        <p>Địa chỉ: Quận Ninh Kiều, TP. Cần Thơ, Việt Nam</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Nhúng footer.jsp -->
        <jsp:include page="footer.jsp" />
    </body>
</html>