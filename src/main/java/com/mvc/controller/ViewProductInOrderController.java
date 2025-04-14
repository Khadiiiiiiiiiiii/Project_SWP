/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mvc.controller;

import com.mvc.DAO.OrderDAO;
import com.mvc.model.Order;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author hoang
 */

public class ViewProductInOrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int order_id = Integer.parseInt(request.getParameter("order_id"));
        OrderDAO dao = new OrderDAO();
        Order order = dao.getOrderProductManagementByIdforDetail(order_id);

        request.setAttribute("order", order);
        RequestDispatcher dispatcher = request.getRequestDispatcher("ViewProductInOrder.jsp");
        dispatcher.forward(request, response);
    }
}