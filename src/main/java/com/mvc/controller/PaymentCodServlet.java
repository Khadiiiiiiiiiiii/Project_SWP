package com.mvc.controller;

import com.mvc.DAO.CartDAO;
import com.mvc.DAO.CustomerDAO;
import com.mvc.DAO.OrderDAO;
import com.mvc.DAO.PaymentDAO;
import com.mvc.DAO.PromotionDAO;
import static com.mvc.controller.VnpayController.convertScientificToLong;
import com.mvc.model.CartItem;
import com.mvc.model.Order;
import com.mvc.model.OrderDetail;
import com.mvc.model.Payment;
import com.mvc.model.Promotion;
import com.mvc.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lenam
 */
public class PaymentCodServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PaymentCodServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PaymentCodServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        User user = (User) session.getAttribute("user");
        CustomerDAO customerDao = new CustomerDAO();
        int customerId = customerDao.getCustomerIdByUserId(user.getUserId());
        
        // Lấy thông tin từ request
        String totalPrice_raw = request.getParameter("totalPrice");
        long originalAmount = convertScientificToLong(totalPrice_raw); // Tổng tiền trước giảm giá
        String promotionCode = request.getParameter("cartPromoCode");
        
        // Khởi tạo các biến cho mã giảm giá
        PromotionDAO promotionDao = new PromotionDAO();
        OrderDAO orderDao = new OrderDAO();
        PaymentDAO paymentDao = new PaymentDAO();
        Promotion promotion = promotionDao.getPromotionByCode(promotionCode);
        int promotionId = -1;
        long finalAmount = originalAmount; // Tổng tiền sau giảm giá
        String promoCode = null;
        double discountPercentage = 0.0;
        double discountAmount = 0.0;

        // Tính toán giảm giá nếu có mã khuyến mãi
        if (promotion != null) {
            promotionId = promotion.getPromotionId();
            promoCode = promotion.getCode();
            discountPercentage = promotion.getDiscountPercentage().doubleValue();
            BigDecimal discount = promotion.getDiscountPercentage().divide(new BigDecimal("100"));
            BigDecimal discountAmountBigDecimal = new BigDecimal(originalAmount).multiply(discount);
            discountAmount = discountAmountBigDecimal.doubleValue();
            finalAmount = originalAmount - (long) discountAmount; // Tổng tiền sau giảm giá
        }

        // Tạo Payment
        Payment payment = paymentDao.createPayment(finalAmount);
        String shipping_address = user.getAddress();

        // Tạo Order với thông tin mã giảm giá
        Order order = orderDao.createOrder(
                customerId,
                finalAmount,
                LocalDate.now(),
                shipping_address,
                payment.getPayment_id(),
                promotionId,
                promoCode,
                discountPercentage,
                discountAmount
        );

        // Tạo OrderDetails
        CartDAO cartDao = new CartDAO();
        List<CartItem> cartItems = cartDao.getCartItems(customerId);
        for (CartItem cartItem : cartItems) {
            OrderDetail od = orderDao.createOrderDetail(
                    order.getOrder_id(),
                    cartItem.getProductId(),
                    cartItem.getQuantity(),
                    cartItem.getPrice()
            );
        }

        // Cập nhật số lượng sản phẩm và trạng thái đơn hàng
        List<OrderDetail> listOrderDetail = orderDao.getListOrderDetailByOrderId(order.getOrder_id());
        orderDao.updateQuantityProduct(listOrderDetail);
        orderDao.updateStatusOrder(order.getOrder_id());

        // Chuyển hướng đến lịch sử đơn hàng
        request.getRequestDispatcher("historyOrder").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}