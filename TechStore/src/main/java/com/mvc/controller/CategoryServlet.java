package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.model.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/category"})
public class CategoryServlet extends HttpServlet {
    private ProductDAO productDAO;

    public CategoryServlet() {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryIdStr = request.getParameter("category");
        List<Product> productList = null;
        Map<Integer, List<Product>> productsByCategory = new HashMap<>();
        String categoryName = "All Products";
        boolean isAllProducts = false;

        if (categoryIdStr == null || categoryIdStr.equals("all")) {
            isAllProducts = true;
            productList = productDAO.getAllProducts();
            // Chia sản phẩm theo danh mục
            for (Product product : productList) {
                int categoryId = product.getCategoryId();
                productsByCategory.computeIfAbsent(categoryId, k -> new ArrayList<>()).add(product);
            }
        } else {
            try {
                int categoryId = Integer.parseInt(categoryIdStr);
                productList = productDAO.getProductsByCategoryId(categoryId);
                switch (categoryId) {
                    case 1: categoryName = "Laptop"; break;
                    case 2: categoryName = "Mouse"; break;
                    case 3: categoryName = "Keyboard"; break;
                    case 4: categoryName = "Screen"; break;
                    case 5: categoryName = "Headphone"; break;
                    default: productList = productDAO.getAllProducts(); isAllProducts = true;
                }
            } catch (NumberFormatException e) {
                productList = productDAO.getAllProducts();
                isAllProducts = true;
            }
        }

        if (productList == null) {
            productList = new ArrayList<>();
        }

        request.setAttribute("productList", productList);
        request.setAttribute("productsByCategory", productsByCategory);
        request.setAttribute("categoryName", categoryName);
        request.setAttribute("isAllProducts", isAllProducts);

        System.out.println("Product list size: " + productList.size());
        System.out.println("Products by category: " + productsByCategory.size());

        request.getRequestDispatcher("/products.jsp").forward(request, response);
    }
}