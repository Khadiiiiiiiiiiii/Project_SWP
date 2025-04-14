/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.mvc.controller;

import com.mvc.DAO.OrderDAO;
import com.mvc.DAO.ProductDAO;
import com.mvc.model.Order;
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
 * @author hoang
 */
public class OrderManagementDetailController extends HttpServlet {
   private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO;

    public OrderManagementDetailController() {
        super();
        orderDAO = new OrderDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String order_idParam = request.getParameter("order_id");
            if (order_idParam != null) {
                int order_id = Integer.parseInt(order_idParam);
                Order order = orderDAO.getOrderManagementByIdforDetail(order_id);
                
                if (order != null) {
                    request.setAttribute("order", order);
                    request.getRequestDispatcher("OrderManagementDetail.jsp").forward(request, response);
                } else {
                    request.setAttribute("errorMessage", "Not found!");
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Invalid!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
