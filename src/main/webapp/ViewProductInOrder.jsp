<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.Order" %>
<%@ page import="com.mvc.model.ViewProductInOrder" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    DecimalFormat df = new DecimalFormat("#,###.00");
%>

<html>
<head>
    <title>View Products in Order</title>
    <!-- Thêm Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="CSS/ViewProductInOrder.css">
</head>
<body>
    <div class="container">
        <h2 class="text-center mb-4">View Products in Order</h2>

        <%
            Order order = (Order) request.getAttribute("order");
            if (order != null) {
        %>
        <div class="mb-3">
            <p><strong>Order ID:</strong> <%= order.getOrder_id() %></p>
            <p><strong>Status:</strong> <span class="badge bg-success"><%= order.getOrder_status() %></span></p>
        </div>

        <div class="table-container">
            <table class="table table-bordered text-center">
                <thead>
                    <tr>
                        <th>No.</th>
                        <th>Product Name</th>
                        <th>Quantity</th>
                        <th>Unit Price</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        int index = 1;
                        for (ViewProductInOrder detail : order.getViewProductInOrders()) {
                    %>
                    <tr>
                        <td><%= index++ %></td>
                        <td class="text-start"><%= detail.getProductName() %></td>
                        <td><%= detail.getQuantity() %></td>
                        <td><%= df.format(detail.getUnitPrice()) %></td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
        <%
            } else {
        %>
        <p class="text-danger text-center">No order found.</p>
        <%
            }
        %>
        <a class="btn btn-secondary" href="ListOrderManagement">Back</a>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
