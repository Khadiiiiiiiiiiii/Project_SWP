package com.mvc.controller;

import com.mvc.DAO.CustomerDAO;
import com.mvc.DAO.ReviewDAO;
import com.mvc.model.Review;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;

public class ReviewServlet extends HttpServlet {

    private ReviewDAO reviewDAO = new ReviewDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if ("view".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            request.setAttribute("reviews", reviewDAO.getReviewsByProductId(productId));
            request.getRequestDispatcher("/detail.jsp").forward(request, response);
            return;
        }

        if (user == null) {
            response.sendRedirect("login.jsp?redirect=/ReviewServlet?action=view&productId=" + request.getParameter("productId"));
            return;
        }

        int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
        if (customerId == -1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Customer not found.");
            return;
        }

        if ("createForm".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            request.setAttribute("productId", productId);
            request.getRequestDispatcher("/createReview.jsp").forward(request, response);
        } else if ("viewMyReview".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            Review review = reviewDAO.getReviewByCustomerAndProduct(productId, customerId);
            request.setAttribute("review", review);
            request.getRequestDispatcher("/viewReview.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
        if (customerId == -1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Customer not found.");
            return;
        }

        String action = request.getParameter("action");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        try {
            if ("create".equals(action)) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                int rating = Integer.parseInt(request.getParameter("rating"));
                String comment = request.getParameter("comment");
                if (rating < 1 || rating > 5) {
                    result.put("success", false);
                    result.put("error", "Rating must be between 1 and 5.");
                } else if (reviewDAO.getReviewByCustomerAndProduct(productId, customerId) != null) {
                    result.put("success", false);
                    result.put("error", "You have already reviewed this product.");
                } else {
                    boolean success = reviewDAO.createReview(productId, customerId, rating, comment);
                    result.put("success", success);
                    if (success) {
                        result.put("message", "Review created successfully.");
                    } else {
                        result.put("error", "Failed to create review.");
                    }
                }
            } else if ("update".equals(action)) {
                int reviewId = Integer.parseInt(request.getParameter("reviewId"));
                int rating = Integer.parseInt(request.getParameter("rating"));
                String comment = request.getParameter("comment");
                if (rating < 1 || rating > 5) {
                    result.put("success", false);
                    result.put("error", "Rating must be between 1 and 5.");
                } else {
                    boolean success = reviewDAO.updateReview(reviewId, rating, comment);
                    result.put("success", success);
                    if (success) {
                        result.put("message", "Review updated successfully.");
                    } else {
                        result.put("error", "Failed to update review.");
                    }
                }
            } else if ("delete".equals(action)) {
                int reviewId = Integer.parseInt(request.getParameter("reviewId"));
                Review review = reviewDAO.getReviewByCustomerAndProduct(
                    Integer.parseInt(request.getParameter("productId")), customerId);
                if (review != null && review.getReviewId() == reviewId) {
                    boolean success = reviewDAO.deleteReview(reviewId);
                    result.put("success", success);
                    if (success) {
                        result.put("message", "Review deleted successfully.");
                    } else {
                        result.put("error", "Failed to delete review.");
                    }
                } else {
                    result.put("success", false);
                    result.put("error", "You are not authorized to delete this review.");
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Server error: " + e.getMessage());
            e.printStackTrace();
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}