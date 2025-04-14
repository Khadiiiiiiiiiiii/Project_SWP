/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mvc.controller;

import com.mvc.DAO.OrderDAO;
import com.mvc.model.OrderDetail;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author admin
 */
@WebServlet(name = "VNPResponeController", urlPatterns = {"/vnpayresponse"})
public class VNPResponeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String amountStr = request.getParameter("vnp_Amount");
        double amount = Double.parseDouble(amountStr) / 100;
        PrintWriter out = response.getWriter();
        if ("00".equals(vnp_ResponseCode)) {
            User user = (User) session.getAttribute("user");
            OrderDAO od = new OrderDAO();
            int orderId = (int) session.getAttribute("orderId");
            List<OrderDetail> listOrderDetail = od.getListOrderDetailByOrderId(orderId);
            od.updateQuantityProduct(listOrderDetail);
            od.updateStatusOrder(orderId);
            request.setAttribute("message", "Pay successful!");
        } else {
            request.setAttribute("message", "Pay failed!");
        }
        response.sendRedirect("CartServlet");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
