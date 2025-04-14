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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;


public class viewProductDetailManagement extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;

    public viewProductDetailManagement() {
        super();
        productDAO = new ProductDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String productIdParam = request.getParameter("productId");
            if (productIdParam != null) {
                int productId = Integer.parseInt(productIdParam);
                Product product = productDAO.getProductManagementByIdforDetail(productId);
                
                if (product != null) {
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("viewProductDetailManagement.jsp").forward(request, response);
                } else {
                    request.setAttribute("errorMessage", "Product not found!");
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Invalid product ID!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid product ID format!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}

// <a href="viewProductDetailManagement?productId=${p.productId}" class="btn btn-info btn-sm">Detail</a>