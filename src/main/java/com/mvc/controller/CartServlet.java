package com.mvc.controller;

import com.mvc.DAO.CartItemDAO;
import com.mvc.DAO.CustomerDAO;
import com.mvc.DAO.ProductDAO;
import com.mvc.model.CartItem;
import com.mvc.model.Product;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.io.IOException;
public class CartServlet extends HttpServlet {

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    CustomerDAO customerDAO = new CustomerDAO();
    int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
    if (customerId == -1) {
        response.sendRedirect("error.jsp");
        return;
    }

    CartItemDAO cartItemDAO = new CartItemDAO();
    String action = request.getParameter("action");

    if (action != null) {
        try {
            int productId = Integer.parseInt(request.getParameter("product_id"));
            switch (action) {
                case "add":
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    cartItemDAO.addCartItem(customerId, productId, quantity);
                    break;
                case "update":
                    int cartItemId = Integer.parseInt(request.getParameter("cart_item_id"));
                    int newQuantity = Integer.parseInt(request.getParameter("quantity"));
                    cartItemDAO.updateCartItem(cartItemId, newQuantity);
                    break;
                case "remove":
                    cartItemId = Integer.parseInt(request.getParameter("cart_item_id"));
                    cartItemDAO.removeCartItem(cartItemId);
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // After any changes, redirect to the CartServlet to reload the cart
    response.sendRedirect("CartServlet");
}

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    CustomerDAO customerDAO = new CustomerDAO();
    int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
    if (customerId == -1) {
        response.sendRedirect("error.jsp");
        return;
    }

    CartItemDAO cartItemDAO = new CartItemDAO();
    List<CartItem> cartItems = cartItemDAO.getCartItems(customerId);

    ProductDAO productDAO = new ProductDAO();
    double totalPrice = 0;

    // If cart is empty, ensure we don't get errors when trying to display items
    if (cartItems.isEmpty()) {
        request.setAttribute("emptyCart", true);  // Flag to show cart is empty
    }

    for (CartItem item : cartItems) {
        Product product = productDAO.getProductById(item.getProductId());
        if (product != null) {
            totalPrice += product.getPrice().doubleValue() * item.getQuantity();
        }
    }

    request.setAttribute("cartItems", cartItems);
    request.setAttribute("totalPrice", totalPrice);
    request.getRequestDispatcher("cart.jsp").forward(request, response);
}

}
