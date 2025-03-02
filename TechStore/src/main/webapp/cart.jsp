<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.model.User, com.mvc.DAO.CustomerDAO, com.mvc.DAO.CartItemDAO, com.mvc.DAO.ProductDAO, com.mvc.model.CartItem, com.mvc.model.Product, java.util.List" %>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp"); // If not logged in, redirect to login page
        return;
    }
%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Shopping Cart</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f8f8;
            margin: 0;
            padding: 0;
        }

        header {
            background-color: #333;
            padding: 20px;
            color: white;
            text-align: center;
        }

        h2 {
            color: #333;
            text-align: center;
            margin-top: 20px;
        }

        .cart-container {
            width: 80%;
            margin: 30px auto;
            background-color: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .cart-container table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        .cart-container th,
        .cart-container td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        .cart-container th {
            background-color: #f1f1f1;
        }

        .cart-container tr:hover {
            background-color: #f9f9f9;
        }

        .cart-container .price-column {
            width: 15%;
        }

        .cart-container .actions-column {
            width: 20%;
        }

        input[type="number"] {
            padding: 5px;
            width: 50px;
            margin-right: 10px;
        }

        input[type="submit"], .cart-container .btn {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            border-radius: 5px;
            text-align: center;
        }

        input[type="submit"]:hover, .cart-container .btn:hover {
            background-color: #45a049;
        }

        .continue-shopping {
            text-align: center;
            margin-top: 20px;
        }

        .continue-shopping input {
            background-color: #007bff;
            padding: 10px 20px;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        .continue-shopping input:hover {
            background-color: #0056b3;
        }

        .total-price {
            font-size: 18px;
            font-weight: bold;
            text-align: right;
            margin-top: 20px;
        }

        .checkout-btn {
            display: block;
            width: 100%;
            background-color: #28a745;
            color: white;
            padding: 12px;
            border: none;
            cursor: pointer;
            font-size: 16px;
            margin-top: 20px;
            border-radius: 5px;
        }

        .checkout-btn:hover {
            background-color: #218838;
        }

        .empty-cart-message {
            text-align: center;
            font-size: 18px;
        }
    </style>
</head>
<body>

    <%@ include file="navbar.jsp" %>

    <div class="cart-container">
        <h2>Your Cart</h2>

        <%
            CartItemDAO cartItemDAO = new CartItemDAO();
            ProductDAO productDAO = new ProductDAO();
            User user = (User) session.getAttribute("user");

            // Ensure user is logged in
            if (user == null) {
                response.sendRedirect("login.jsp"); // If not logged in, redirect to login page
                return;
            }

            // Get customerId based on user
            CustomerDAO customerDAO = new CustomerDAO();
            int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());

            // Get cart items for the customer
            List<CartItem> cartItems = cartItemDAO.getCartItems(customerId);

            if (cartItems == null || cartItems.isEmpty()) {
        %>
        <p class="empty-cart-message">Your cart is empty.</p>
        <form action="${pageContext.request.contextPath}/products.jsp" class="continue-shopping">
            <input type="submit" value="Continue Shopping">
        </form>
        <%
            } else {
                double totalPrice = 0;
        %>
        <table>
            <thead>
                <tr>
                    <th>Product</th>
                    <th class="price-column">Price</th>
                    <th>Stock</th>
                    <th>Quantity</th>
                    <th class="price-column">Total</th>
                    <th class="actions-column">Actions</th>
                </tr>
            </thead>
            <tbody>
            <%
                for (CartItem cartItem : cartItems) {
                    Product product = productDAO.getProductById(cartItem.getProductId());
                    double total = product.getPrice().doubleValue() * cartItem.getQuantity();
                    totalPrice += total;
            %>
                <tr>
                    <td><%= product.getName() %></td>
                    <td class="price-column">$<%= product.getPrice() %></td>
                    <td><%= product.getStockQuantity() %></td> <!-- Show stock quantity -->
                    <td>
                        <form action="${pageContext.request.contextPath}/CartServlet" method="post">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="cart_item_id" value="<%= cartItem.getCartItemId() %>">
                            <input type="number" name="quantity" value="<%= cartItem.getQuantity() %>" min="1" max="<%= product.getStockQuantity() %>">
                            <input type="submit" value="Update">
                        </form>
                    </td>
                    <td class="price-column">$<%= total %></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/CartServlet" method="post">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="cart_item_id" value="<%= cartItem.getCartItemId() %>">
                            <input type="submit" value="Remove" class="btn">
                        </form>
                    </td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>

        <div class="total-price">Total: $<%= totalPrice %></div>

        <form action="${pageContext.request.contextPath}/checkout.jsp">
            <input type="submit" value="Proceed to Checkout" class="checkout-btn">
        </form>
        <%
            }
        %>

        <form action="${pageContext.request.contextPath}/category?category=all" class="continue-shopping">
            <input type="submit" value="Continue Shopping">
        </form>
    </div>

</body>
</html>
