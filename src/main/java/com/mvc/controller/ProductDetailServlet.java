package com.mvc.controller;

import com.mvc.DAO.CustomerDAO;
import com.mvc.DAO.ProductDAO;
import com.mvc.DAO.PromotionDAO;
import com.mvc.model.Product;
import com.mvc.model.Promotion;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/productDetail")
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String productId = request.getParameter("productId");
        ProductDAO productDAO = new ProductDAO();
        PromotionDAO promotionDAO = new PromotionDAO();
        PrintWriter out = response.getWriter();
        User user = (User) session.getAttribute("user");
        if (productId != null) {
            int id = Integer.parseInt(productId);
            Product product = productDAO.getProductById(id);
            request.setAttribute("product", product);
            if (user != null) {
                CustomerDAO customerDAO = new CustomerDAO();
                int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
                int quantityInCart = product.getStockQuantity() - productDAO.getQuantityOfAProductInCart(id, customerId);
                out.print(id + " , " + customerId + " , " + quantityInCart);
                session.setAttribute("quantityInCart", quantityInCart);
            }
            // Lấy danh sách mã giảm giá còn hiệu lực
            List<Promotion> availablePromotions = promotionDAO.getActivePromotions();
            request.setAttribute("availablePromotions", availablePromotions);

            request.getRequestDispatcher("/productDetail.jsp").forward(request, response);
        } else {
            response.sendRedirect("category?category=all");
        }
    }
}
