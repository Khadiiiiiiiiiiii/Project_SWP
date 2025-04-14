package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.DAO.UserDAO;
import com.mvc.model.Product;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Servlet to handle requests related to updating product discounts directly by Store Manager.
 */
@WebServlet("/ProductDiscountServlet")
public class ProductDiscountServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !("Store Manager".equals(user.getRole()) || "store manager".equals(user.getRole()))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String storeId = request.getParameter("storeId"); // May not be needed if checked via session

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try ( PrintWriter out = response.getWriter()) {
            if ("updateDiscount".equals(action)) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                double discountPercentage = Double.parseDouble(request.getParameter("discountPercentage"));

                if (discountPercentage < 0 || discountPercentage > 100) {
                    out.print("{\"error\": \"Discount percentage must be between 0 and 100\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                // Check if the product exists
                int storeManagerStoreId = userDAO.getStoreIdByUserId(user.getUserId());
                Product product = productDAO.getProductById(productId);
                if (product == null) {
                    out.print("{\"error\": \"Product not found\"}");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // Update the discount
                productDAO.updateProductDiscount(productId, new BigDecimal(discountPercentage));

                // Set success message in session
                session.setAttribute("message", "Discount updated successfully!");

                // Redirect to ManageProductDiscounts.jsp
                response.sendRedirect(request.getContextPath() + "/ManageProductDiscounts.jsp");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try ( PrintWriter out = response.getWriter()) {
                out.print("{\"error\": \"Invalid parameter format\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try ( PrintWriter out = response.getWriter()) {
                out.print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // No GET handling needed, redirect to login
        response.sendRedirect("login.jsp");
    }
}
