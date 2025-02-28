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

/**
 *
 * @author Nguyen
 */
@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/getProductDetails"})
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String productId = request.getParameter("productId"); // Lấy productId từ URL

        if (productId != null && !productId.isEmpty()) {
            ProductDAO productDAO = new ProductDAO();
            Product product = productDAO.getProductById(Integer.parseInt(productId)); // Lấy sản phẩm từ DB

            if (product != null) {
                // Trả về dữ liệu sản phẩm dưới dạng JSON
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{");
                out.print("\"name\":\"" + product.getName() + "\",");
                out.print("\"price\":\"" + product.getPrice() + "\",");
                out.print("\"description\":\"" + product.getDescription() + "\",");
                out.print("\"imageUrl\":\"" + product.getImageUrl() + "\"");
                out.print("}");
            } else {
                // Trường hợp không tìm thấy sản phẩm
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"message\":\"Product not found\"}");
            }
        } else {
            // Trường hợp thiếu productId
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"message\":\"Missing productId\"}");
        }
    }
}
