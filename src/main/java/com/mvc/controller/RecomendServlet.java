package com.mvc.controller;

import com.mvc.DAO.RecomendDAO;
import com.mvc.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class RecomendServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RecomendDAO recomendDAO = new RecomendDAO();
        List<Product> topRatedProducts = recomendDAO.getTopRatedProducts(6);
        List<Product> bestSellingProducts = recomendDAO.getBestSellingProducts(6);

        request.setAttribute("bestSellingProducts", bestSellingProducts);
        request.setAttribute("topRatedProducts", topRatedProducts);
        request.getRequestDispatcher("Home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
