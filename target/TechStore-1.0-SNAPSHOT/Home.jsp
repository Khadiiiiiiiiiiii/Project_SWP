<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.mvc.DAO.RecomendDAO" %>
<%@ page import="com.mvc.model.Product" %>
<%@ page import="java.util.List" %>

<%
    RecomendDAO recomendDAO = new RecomendDAO();
    List<Product> topRatedProducts = recomendDAO.getTopRatedProducts(6);
    List<Product> bestSellingProducts = recomendDAO.getBestSellingProducts(6);
    request.setAttribute("bestSellingProducts", bestSellingProducts);
    request.setAttribute("topRatedProducts", topRatedProducts);
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tech Store</title>
        <link rel="stylesheet" href="CSS/Home.css">
    </head>
    <body>
        <%@ include file="navbar.jsp" %>
        
        <main class="hero">
            <video class="hero-video" autoplay muted loop>
                <source src="img/background_vid.mp4" type="video/mp4">
                Your browser does not support the video tag.
            </video>
            <div class="hero-content">
                <h1>Latest Tech, Best Price!</h1>
                <div class="button-group">
                    <a href="category?category=all" class="btn btn-primary">SHOP NOW</a>
                    <a href="#best-selling-section" class="btn btn-best-selling">⚡ Best-Selling</a>
                </div>
            </div>
        </main>
        
        <!-- Recommend Section -->
        <%@ include file="recommend.jsp" %>

        <%@ include file="footer.jsp" %>

        <!-- JavaScript để cuộn mượt -->
        <script>
            document.querySelector('.btn-best-selling').addEventListener('click', function(e) {
                e.preventDefault(); // Ngăn hành vi mặc định của thẻ <a>
                const bestSellingSection = document.querySelector('#best-selling-section');
                if (bestSellingSection) {
                    bestSellingSection.scrollIntoView({ behavior: 'smooth' });
                }
            });
        </script>
    </body>
</html>