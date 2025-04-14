package com.mvc.controller;

import com.mvc.DAO.CustomerDAO;
import com.mvc.DAO.UserDAO;
import com.mvc.dal.DBContext;
import com.mvc.model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/UpdateCustomerServlet")
public class UpdateCustomerServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty() || userIdStr.matches("^\\s+$")) {
            response.sendRedirect("editCustomer.jsp?error=missing_id");
            return;
        }

        Connection connection = null;
        try {
            int userId = Integer.parseInt(userIdStr);
            String email = request.getParameter("email");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String loyaltyPointsStr = request.getParameter("loyaltyPoints");
            String preferredPaymentMethod = request.getParameter("preferredPaymentMethod");

            List<String> errors = new ArrayList<>();

            // Kiểm tra dữ liệu đầu vào
            if (email == null || email.trim().isEmpty() || email.matches("^\\s+$")) {
                errors.add("Email cannot be empty or contain only spaces.");
            } else if (!isValidEmail(email)) {
                errors.add("Please enter a valid email address.");
            }

            if (firstName == null || firstName.trim().isEmpty() || firstName.matches("^\\s+$")) {
                errors.add("First name cannot be empty or contain only spaces.");
            }

            if (lastName == null || lastName.trim().isEmpty() || lastName.matches("^\\s+$")) {
                errors.add("Last name cannot be empty or contain only spaces.");
            }

            if (phone == null || phone.trim().isEmpty() || phone.matches("^\\s+$")) {
                errors.add("Phone number cannot be empty or contain only spaces.");
            } else if (!isValidPhone(phone)) {
                errors.add("Phone number must be 10-15 digits.");
            }

            if (address == null || address.trim().isEmpty() || address.matches("^\\s+$")) {
                errors.add("Address cannot be empty or contain only spaces.");
            }

            if (preferredPaymentMethod == null || preferredPaymentMethod.trim().isEmpty() || preferredPaymentMethod.matches("^\\s+$")) {
                errors.add("Preferred payment method cannot be empty or contain only spaces.");
            }

            Integer loyaltyPoints = 0;
            if (loyaltyPointsStr == null || loyaltyPointsStr.trim().isEmpty() || loyaltyPointsStr.matches("^\\s+$")) {
                errors.add("Loyalty points cannot be empty or contain only spaces.");
            } else {
                try {
                    loyaltyPoints = Integer.parseInt(loyaltyPointsStr);
                    if (loyaltyPoints < 0) {
                        errors.add("Loyalty points cannot be negative.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Loyalty points must be a valid integer.");
                }
            }

            // Kết nối database
            connection = DBContext.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);
            UserDAO userDAO = new UserDAO();

            // Lấy thông tin hiện tại của khách hàng
            Customer currentCustomer = customerDAO.getCustomerByID(userId);
            String currentEmail = (currentCustomer != null) ? currentCustomer.getEmail() : "";
            String currentPhone = (currentCustomer != null) ? currentCustomer.getPhone() : "";

            // Kiểm tra email mới
            if (!email.trim().equals(currentEmail)) {
                if (userDAO.isEmailExist(email)) {
                    errors.add("Email already exists. Please use a different email address.");
                }
            }

            // Kiểm tra số điện thoại mới
            if (!phone.trim().equals(currentPhone)) {
                if (userDAO.isPhoneExistForOtherUsers(phone, userId)) {
                    errors.add("Phone number already exists. Please use a different phone number.");
                }
            }

            // Nếu có lỗi, chuyển hướng với thông báo lỗi
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("userId", userId);
                request.setAttribute("email", email);
                request.setAttribute("firstName", firstName);
                request.setAttribute("lastName", lastName);
                request.setAttribute("phone", phone);
                request.setAttribute("address", address);
                request.setAttribute("loyaltyPoints", loyaltyPointsStr);
                request.setAttribute("preferredPaymentMethod", preferredPaymentMethod);
                request.getRequestDispatcher("editCustomer.jsp").forward(request, response);
                return;
            }

            // Tạo đối tượng Customer
            Customer customer = new Customer();
            customer.setUserId(userId);
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setLoyaltyPoints(loyaltyPoints);
            customer.setPreferredPaymentMethod(preferredPaymentMethod);

            // Cập nhật dữ liệu
            boolean updated = customerDAO.updateCustomer(customer);

            if (updated) {
                response.sendRedirect("CustomerManagement.jsp?success=updated");
            } else {
                response.sendRedirect("editCustomer.jsp?userId=" + userId + "&error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("editCustomer.jsp?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("editCustomer.jsp?error=exception");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    // Kiểm tra số điện thoại hợp lệ (10-15 chữ số)
    private boolean isValidPhone(String phone) {
        String phoneRegex = "^[0-9]{10,15}$";
        return Pattern.matches(phoneRegex, phone);
    }
}
