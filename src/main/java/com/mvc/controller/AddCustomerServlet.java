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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/AddCustomerServlet")
public class AddCustomerServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Lấy dữ liệu từ form
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String loyaltyPointsStr = request.getParameter("loyaltyPoints");
            String preferredPaymentMethod = request.getParameter("preferredPaymentMethod");

            // Regex cho validation
            String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
            String nameRegex = "^[a-zA-Z\\s]+$";
            String phoneRegex = "^\\d{10}$";
            String passwordRegex = "^(?=.*[A-Za-z]).{6,}$"; // Ít nhất 6 ký tự và có ít nhất một chữ cái

            // Thu thập tất cả lỗi vào một danh sách
            List<String> errors = new ArrayList<>();

            // Kiểm tra trường không được chỉ chứa dấu cách
            if (email == null || email.trim().isEmpty() || email.matches("^\\s+$")) {
                errors.add("Email cannot be empty or contain only spaces.");
            } else if (!Pattern.matches(emailRegex, email)) {
                errors.add("Please enter a valid email address.");
            }

            // Kiểm tra email đã tồn tại chưa
            UserDAO userDAO = new UserDAO();
            try {
                if (userDAO.isEmailExist(email)) {
                    errors.add("Email already exists. Please use a different email address.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                errors.add("Error checking email. Please try again.");
            }

            if (password == null || password.trim().isEmpty() || password.matches("^\\s+$")) {
                errors.add("Password cannot be empty or contain only spaces.");
            } else if (!Pattern.matches(passwordRegex, password) || password.contains(" ") || !password.equals(confirmPassword)) {
                errors.add("Password must be at least 6 characters, contain at least one letter, no spaces, and match the confirmation.");
            }

            if (firstName == null || firstName.trim().isEmpty() || firstName.matches("^\\s+$")) {
                errors.add("First name cannot be empty or contain only spaces.");
            } else if (!Pattern.matches(nameRegex, firstName)) {
                errors.add("First name must contain only letters.");
            }

            if (lastName == null || lastName.trim().isEmpty() || lastName.matches("^\\s+$")) {
                errors.add("Last name cannot be empty or contain only spaces.");
            } else if (!Pattern.matches(nameRegex, lastName)) {
                errors.add("Last name must contain only letters.");
            }

            if (phone == null || phone.trim().isEmpty() || phone.matches("^\\s+$")) {
                errors.add("Phone number cannot be empty or contain only spaces.");
            } else if (!Pattern.matches(phoneRegex, phone)) {
                errors.add("Phone number must be exactly 10 digits.");
            }

            // Kiểm tra số điện thoại đã tồn tại chưa
            try {
                if (userDAO.isPhoneExist(phone)) {
                    errors.add("Phone number already exists. Please use a different phone number.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                errors.add("Error checking phone number. Please try again.");
            }

            int loyaltyPoints = 0;
            try {
                loyaltyPoints = Integer.parseInt(loyaltyPointsStr);
                if (loyaltyPoints < 0) {
                    errors.add("Loyalty points cannot be negative.");
                }
            } catch (NumberFormatException e) {
                errors.add("Loyalty points must be a valid integer.");
            }

            if (address == null || address.trim().isEmpty() || address.matches("^\\s+$")) {
                errors.add("Address cannot be empty or contain only spaces.");
            }

            if (preferredPaymentMethod == null || preferredPaymentMethod.trim().isEmpty() || preferredPaymentMethod.matches("^\\s+$")) {
                errors.add("Preferred payment method cannot be empty or contain only spaces.");
            }

            // Nếu có lỗi, gửi thông báo lỗi và dữ liệu form về JSP
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("email", email);
                request.setAttribute("firstName", firstName);
                request.setAttribute("lastName", lastName);
                request.setAttribute("phone", phone);
                request.setAttribute("address", address);
                request.setAttribute("loyaltyPoints", loyaltyPointsStr);
                request.setAttribute("preferredPaymentMethod", preferredPaymentMethod);
                request.getRequestDispatcher("addCustomer.jsp").forward(request, response);
                return;
            }

            // Nếu không có lỗi, thêm khách hàng
            Customer customer = new Customer();
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setLoyaltyPoints(loyaltyPoints);
            customer.setPreferredPaymentMethod(preferredPaymentMethod);

            Connection connection = DBContext.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);
            boolean added = customerDAO.addCustomer(customer, password);

            if (added) {
                response.sendRedirect("CustomerManagement.jsp?success=added");
            } else {
                errors.add("Failed to add customer. Possibly the email already exists.");
                request.setAttribute("errors", errors);
                request.setAttribute("email", email);
                request.setAttribute("firstName", firstName);
                request.setAttribute("lastName", lastName);
                request.setAttribute("phone", phone);
                request.setAttribute("address", address);
                request.setAttribute("loyaltyPoints", loyaltyPointsStr);
                request.setAttribute("preferredPaymentMethod", preferredPaymentMethod);
                request.getRequestDispatcher("addCustomer.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            List<String> errors = new ArrayList<>();
            errors.add("An unexpected error occurred. Please try again.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("addCustomer.jsp").forward(request, response);
        }
    }
}
