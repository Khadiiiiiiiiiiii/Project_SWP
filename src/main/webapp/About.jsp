<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Giới thiệu - TechTrend Innovations</title>
        <!-- Thêm Font Awesome để sử dụng biểu tượng -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            /* Đảm bảo footer luôn nằm dưới đáy */
            html, body {
                height: 100%;
                margin: 0;
                padding: 0;
                display: flex;
                flex-direction: column;
            }

            body {
                font-family: Arial, sans-serif;
                background-color: #f5f5f5;
            }

            .container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 20px;
                flex: 1;
            }

            .about-content {
                background-color: white;
                padding: 30px;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                margin-bottom: 20px;
                text-align: center;
            }

            .about-content h2 {
                color: #333;
                font-size: 2em;
                margin-bottom: 15px;
            }

            .about-content p {
                color: #666;
                line-height: 1.6;
                font-size: 1em;
                margin-bottom: 20px;
            }

            /* CSS cho phần liên hệ (nếu có dùng sau) */
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
        </style>
    </head>
    <body>
        <!-- Nhúng navbar.jsp -->
        <jsp:include page="navbar.jsp" />

        <!-- Nội dung chính -->
        <div class="container">
            <!-- Phần nội dung giới thiệu -->
            <div class="about-content">
                <h2>Giới thiệu về Tech Store</h2>
                <p>
                    Tech Store là một nền tảng thương mại điện tử chuyên cung cấp các sản phẩm công nghệ chất lượng cao như laptop, chuột, bàn phím, màn hình, tai nghe và nhiều phụ kiện khác. Chúng tôi cam kết mang đến trải nghiệm mua sắm trực tuyến tiện lợi, nhanh chóng và đáng tin cậy với giá cả cạnh tranh.
                </p>
            </div>
        </div>

        <!-- Nhúng footer.jsp -->
        <jsp:include page="footer.jsp" />
    </body>
</html>
