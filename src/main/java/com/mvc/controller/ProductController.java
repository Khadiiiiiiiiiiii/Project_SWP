/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.model.Product;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author hoang
 */
@WebServlet("/ProductController")
public class ProductController extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String productIdStr = request.getParameter("productId");

        System.out.println("Received request - Action: " + action + ", ProductId: " + productIdStr);

        try {
            if (action == null || action.trim().isEmpty()) {
                List<Product> proList = productDAO.getAllProductsForManagement();
                request.setAttribute("proList", proList);
                System.out.println("Loading product list with " + (proList != null ? proList.size() : 0) + " products");
                request.getRequestDispatcher("ProductManagement.jsp").forward(request, response);
            } else if (action.equals("delete") || action.equals("restore")) {
                if (productIdStr == null || productIdStr.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Product ID is missing");
                    System.out.println("Error: Product ID is missing");
                } else {
                    int productId = Integer.parseInt(productIdStr);
                    System.out.println("Processing " + action + " for productId: " + productId);
                    boolean isDeleted = action.equals("delete");
                    productDAO.setProductStatus(productId, isDeleted);
                    // Redirect to refresh the page
                    response.sendRedirect(request.getContextPath() + "/ProductController");
                    return; // Exit the method after redirect
                }
            } else {
                request.setAttribute("errorMessage", "Invalid action: " + action);
                System.out.println("Error: Invalid action: " + action);
            }
            // Forward to JSP only if no redirect
            List<Product> proList = productDAO.getAllProductsForManagement();
            request.setAttribute("proList", proList);
            request.getRequestDispatcher("ProductManagement.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid product ID format");
            request.setAttribute("proList", productDAO.getAllProductsForManagement());
            request.getRequestDispatcher("ProductManagement.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Server error: " + e.getMessage());
            request.setAttribute("proList", productDAO.getAllProductsForManagement());
            request.getRequestDispatcher("ProductManagement.jsp").forward(request, response);
        }
    }
}
