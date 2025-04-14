<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.mvc.model.Review, com.mvc.model.Product, com.mvc.model.Customer" %>
<%@ page import="com.mvc.DAO.ProductDAO, com.mvc.DAO.CustomerDAO" %>
<%
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
    ProductDAO productDAO = new ProductDAO();
    CustomerDAO customerDAO = new CustomerDAO();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Reviews Management</title>
        <link rel="stylesheet" href="CSS/CustomerManagement.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <style>

            /* ===== Main Content ===== */
            .main-content {
                margin-left: 240px;
                padding: 40px;
                background-color: #f5f7fa;
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            h1 {
                text-align: center;
                color: #333;
                margin-bottom: 30px;
            }

            /* ===== Table Styling ===== */
            table {
                width: 100%;
                border-collapse: collapse;
                background-color: white;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                border-radius: 8px;
                overflow: hidden;
            }

            /* Table Header */
            th {
                background-color: #1a1a2e; /* Black/dark navy background */
                color: white;
                font-weight: bold;
                text-transform: uppercase;
                font-size: 14px;
                padding: 12px 15px;
                border: 1px solid #2c2c3e;
            }

            /* Rounded corners on top */
            thead th:first-child {
                border-top-left-radius: 8px;
            }

            thead th:last-child {
                border-top-right-radius: 8px;
            }

            /* Table Body */
            td {
                padding: 12px 15px;
                border: 1px solid #ddd;
                text-align: left;
                vertical-align: top;
            }

            tr:nth-child(even) {
                background-color: #f9f9f9;
            }

            tr:hover {
                background-color: #f1f1f1;
            }

            /* ===== Product Image ===== */
            .product-img {
                height: 50px;
                border-radius: 5px;
            }

            /* ===== Review Section ===== */
            .reply-box {
                margin-top: 5px;
                color: green;
                font-style: italic;
            }

            .reply-time {
                color: #555;
                font-size: 12px;
                margin-top: 3px;
                display: block;
            }

            .comment-time {
                margin-top: 5px;
            }

            /* ===== Form ===== */
            form {
                margin-top: 8px;
            }

            input[type="text"] {
                padding: 5px;
                width: 160px;
                border: 1px solid #ccc;
                border-radius: 4px;
            }

            /* ===== Buttons ===== */
            button {
                padding: 5px 10px;
                margin-top: 5px;
                border: none;
                border-radius: 4px;
                background-color: #28a745;
                color: white;
                cursor: pointer;
            }

            button[name="action"][value="delete"] {
                background-color: #dc3545;
            }

            button:hover {
                opacity: 0.9;
            }
        </style>
    </head>
    <body>

        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Store Manager Dashboard</h2>
            </div>
            <nav>
                <ul>
                    <li>
                        <a href="storeManagerDashboard.jsp" class="<%= "storeManagerDashboard.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-tags"></i> Manage Promotions
                        </a>
                    </li>
                    <li>
                        <a href="ManageProductDiscounts.jsp" class="<%= "ManageProductDiscounts.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-percent"></i> Manage Product Discounts
                        </a>
                    </li>

                    <li>
                        <a href="reviewsManagement"
                           class="<%= request.getRequestURI().contains("reviewsManagement") ? "active" : "" %>">
                            <i class="fas fa-comment-dots"></i> Manage Reviews
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement" class="<%= "ProductManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="ListOrderManagement" class="<%= request.getRequestURI().contains("ListOrderManagement") ? "active" : "" %>">
                            <i class="fas fa-shopping-cart"></i> Order Management
                        </a>
                    </li>
                    <li>
                        <a href="logout" class="logout">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </a>
                    </li>
                </ul>
            </nav>
        </div>

        <div class="main-content">
            <h1>Reviews Management</h1>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Product</th>
                        <th>Customer</th>
                        <th>Rating</th>
                        <th>Comment</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (reviews != null && !reviews.isEmpty()) {
                        for (Review review : reviews) {
                            Product product = productDAO.getProductById(review.getProductId());
                            Customer customer = customerDAO.getCustomerById(review.getCustomerId());
                    %>
                    <tr>
                        <td><%= review.getReviewId() %></td>
                        <td>
                            <img src="<%= product.getImageUrl() %>" alt="Product Image" class="product-img"><br>
                            <%= product.getName() %>
                        </td>
                        <td><%= customer.getUser().getFirstName() %> <%= customer.getUser().getLastName() %></td>
                        <td><%= review.getRating() %></td>
                        <td>
                            <%= review.getComment() %>
                            <div class="comment-time">
                                <small style="color: #555;">Commented at:
                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(review.getCreatedAt()) %>
                                </small>
                            </div>

                            <% if (review.getReply() != null && !review.getReply().isEmpty()) { %>
                            <div class="reply-box">
                                <strong>Reply:</strong> <%= review.getReply() %>
                                <% if (review.getReplyAt() != null) { %>
                                <span class="reply-time">Replied at:
                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(review.getReplyAt()) %>
                                </span>
                                <% } %>
                            </div>
                            <% } %>
                        </td>

                        <td>
                            <form method="post" action="reviewsManagement"
                                  onsubmit="return confirm('Are you sure you want to delete this review?');">
                                <input type="hidden" name="reviewId" value="<%= review.getReviewId() %>"/>
                                <button type="submit" name="action" value="delete">Delete</button>
                            </form>
                            <form method="post" action="reviewsManagement">
                                <input type="hidden" name="reviewId" value="<%= review.getReviewId() %>"/>
                                <input type="text" name="reply" placeholder="Enter reply" required/>
                                <button type="submit" name="action" value="respond">Reply</button>
                            </form>
                        </td>
                    </tr>
                    <% } } else { %>
                    <tr><td colspan="6">No reviews available</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>

    </body>
</html>
