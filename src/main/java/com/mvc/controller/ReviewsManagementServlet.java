package com.mvc.controller;

import com.mvc.DAO.ReviewsManagementDAO;
import com.mvc.model.Review;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class ReviewsManagementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ReviewsManagementDAO dao = new ReviewsManagementDAO();
        List<Review> reviews = dao.getAllReviews();

        request.setAttribute("reviews", reviews);
        String message = request.getParameter("message");
        if (message != null) {
            request.setAttribute("message", message);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/reviewsManagementDashboard.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        ReviewsManagementDAO dao = new ReviewsManagementDAO();

        String message = "";
        if ("delete".equals(action)) {
            int reviewId = Integer.parseInt(request.getParameter("reviewId"));
            boolean success = dao.deleteReview(reviewId);
            message = success ? "Review deleted successfully." : "Failed to delete review.";
        } else if ("respond".equals(action)) {
            int reviewId = Integer.parseInt(request.getParameter("reviewId"));
            String reply = request.getParameter("reply");
            boolean success = dao.respondToReview(reviewId, reply);
            message = success ? "Reply submitted successfully." : "Failed to submit reply.";
        }

        response.sendRedirect("reviewsManagement?message=" + URLEncoder.encode(message, "UTF-8"));
    }
}
