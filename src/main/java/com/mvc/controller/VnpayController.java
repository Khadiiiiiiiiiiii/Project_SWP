package com.mvc.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mvc.DAO.CartDAO;
import com.mvc.DAO.CustomerDAO;
import com.mvc.DAO.OrderDAO;
import com.mvc.DAO.PaymentDAO;
import com.mvc.DAO.PromotionDAO;
import com.mvc.model.CartItem;
import com.mvc.model.Customer;
import com.mvc.model.Order;
import com.mvc.model.OrderDetail;
import com.mvc.model.Payment;
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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author admin
 */
@WebServlet(name = "VnpayController", urlPatterns = {"/vnpaycontroller"})
public class VnpayController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        PrintWriter out = resp.getWriter();
        User user = (User) session.getAttribute("user");
        CustomerDAO customerDao = new CustomerDAO();
        int customerId = customerDao.getCustomerIdByUserId(user.getUserId());

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String totalPrice_raw = req.getParameter("totalPrice");
        long originalAmount = convertScientificToLong(totalPrice_raw); // Tổng tiền trước giảm giá
        String promotionCode = req.getParameter("cartPromoCode");

        // Khởi tạo các biến cho mã giảm giá
        PromotionDAO promotionDao = new PromotionDAO();
        PaymentDAO paymentDao = new PaymentDAO();
        OrderDAO orderDao = new OrderDAO();
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

        // Lưu orderId vào session để sử dụng sau khi thanh toán
        session.setAttribute("orderId", order.getOrder_id());

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

        // Tạo URL thanh toán VNPay
        String bankCode = "VNBANK";
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = Config.getIpAddress(req);
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(finalAmount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Payment: " + totalPrice_raw);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
        String paymentUrl = Config.vnp_PayUrl + "?" + query.toString();
        resp.sendRedirect(paymentUrl);
    }

    public static long convertScientificToLong(String scientificNotation) {
        double doubleValue = Double.parseDouble(scientificNotation);
        return (long) doubleValue;
    }
}