<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.mvc.DAO.ReviewDAO, com.mvc.DAO.CustomerDAO, com.mvc.DAO.ProductDAO, com.mvc.DAO.ReviewsManagementDAO, com.mvc.model.Review, com.mvc.model.Product" %>

<%
    pageContext.setAttribute("reviewDAO", new ReviewDAO());
    pageContext.setAttribute("customerDAO", new CustomerDAO());
    ReviewsManagementDAO reviewManagementDAO = new ReviewsManagementDAO();
    ProductDAO productDAO = new ProductDAO();
    pageContext.setAttribute("reviewManagementDAO", reviewManagementDAO);
    pageContext.setAttribute("productDAO", productDAO);
%>


<div class="reviews-section">
    <h3>Product Reviews</h3>
    <c:set var="reviews" value="${reviewDAO.getReviewsByProductId(param.productId)}" />
    <c:if test="${empty reviews}">
        <p>No reviews yet.</p>
    </c:if>
    <c:forEach var="review" items="${reviews}">
        <div class="review">
            <div class="rating">Rating: ${review.rating} / 5</div>
            <div class="comment">${review.comment}</div>

            <div class="author">
                <c:set var="customer" value="${customerDAO.getCustomerById(review.customerId)}" />
                By ${customer.user.firstName} ${customer.user.lastName} -
                Created: <fmt:formatDate value="${review.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                <c:if test="${not empty review.updatedAt and review.updatedAt != review.createdAt}">
                    (Updated: <fmt:formatDate value="${review.updatedAt}" pattern="dd/MM/yyyy HH:mm" />)
                </c:if>
            </div>

            <%-- Show reply using ReviewsManagementDAO --%>
            <%
                com.mvc.model.Review replyInfo = reviewManagementDAO.getReplyInfoByReviewId(((com.mvc.model.Review) pageContext.findAttribute("review")).getReviewId());
                if (replyInfo != null && replyInfo.getReply() != null && !replyInfo.getReply().trim().isEmpty()) {
            %>
            <div class="reply-wrapper">
                <div class="reply-arrow">&#x21B3;</div>
                <div class="reply">
                    <strong>Reply from Store Manager:</strong> <%= replyInfo.getReply() %>
                    <% if (replyInfo.getReplyAt() != null) { %>
                    <br><small style="color: #777;">Replied at: <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(replyInfo.getReplyAt()) %></small>
                    <% } %>
                </div>
            </div>
            <%
                }
            %>

        </div>
    </c:forEach>
</div>


<c:if test="${not empty sessionScope.user}">
    <c:set var="customerId" value="${customerDAO.getCustomerIdByUserId(sessionScope.user.userId)}" />
    <div class="review-form">
        <h3>Leave a Review</h3>
        <c:choose>
            <c:when test="${customerId > 0 and fn:toLowerCase(sessionScope.user.role) eq 'customer'}">
                <form id="createReviewForm" action="${pageContext.request.contextPath}/ReviewServlet" method="post">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="productId" value="${param.productId}">
                    <input type="hidden" name="customerId" value="${customerId}">
                    <label for="rating">Rating (1-5):</label>
                    <input type="number" id="rating" name="rating" min="1" max="5" required>
                    <label for="comment">Comment:</label>
                    <textarea id="comment" name="comment" rows="4" required></textarea>
                    <button type="submit">Submit Review</button>
                </form>

                <c:set var="myReview" value="${reviewDAO.getReviewByCustomerAndProduct(param.productId, customerId)}" />
                <c:if test="${not empty myReview}">
                    <div class="my-review-actions">
                        <h4>My Review</h4>
                        <p>Rating: ${myReview.rating} / 5</p>
                        <p>Comment: ${myReview.comment}</p>
                        <p>Created: <fmt:formatDate value="${myReview.createdAt}" pattern="dd/MM/yyyy HH:mm" /></p>
                        <c:if test="${not empty myReview.updatedAt and myReview.updatedAt != myReview.createdAt}">
                            <p>Updated: <fmt:formatDate value="${myReview.updatedAt}" pattern="dd/MM/yyyy HH:mm" /></p>
                        </c:if>
                        <button onclick="showUpdateForm(${myReview.reviewId}, ${myReview.rating}, '${myReview.comment}')">Update</button>
                        <button onclick="deleteReview(${myReview.reviewId}, ${param.productId})">Delete</button>
                    </div>
                    <form id="updateReviewForm" style="display:none;" action="${pageContext.request.contextPath}/ReviewServlet" method="post">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" id="updateReviewId" name="reviewId">
                        <label for="updateRating">Rating (1-5):</label>
                        <input type="number" id="updateRating" name="rating" min="1" max="5" required>
                        <label for="updateComment">Comment:</label>
                        <textarea id="updateComment" name="comment" rows="4" required></textarea>
                        <button type="submit">Update Review</button>
                    </form>
                </c:if>
            </c:when>
            <c:otherwise>
                <p class="error-message">You need to be registered as a Customer to leave a review. Please contact an admin or check your account.</p>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>
<c:if test="${empty sessionScope.user}">
    <p class="login-prompt">Please <a href="login.jsp">login</a> to leave, update, or delete a review.</p>
</c:if>

<style>
    .reviews-section {
        margin-top: 30px;
    }
    .review {
        border: 1px solid #ddd;
        padding: 10px;
        margin-bottom: 10px;
        border-radius: 5px;
        background-color: #f9f9f9;
    }
    .review .rating {
        color: #ff9800;
        font-weight: bold;
    }
    .review .comment {
        margin-top: 5px;
        font-size: 14px;
    }
    .review .author {
        font-style: italic;
        color: #666;
        font-size: 12px;
        margin-top: 5px;
    }
    .product-info {
        margin: 5px 0;
        display: flex;
        align-items: center;
    }
    .product-info img.product-thumbnail {
        width: 40px;
        height: 40px;
        object-fit: cover;
        border-radius: 4px;
        margin-right: 10px;
        border: 1px solid #ccc;
    }
    .review-form {
        margin-top: 20px;
        padding: 15px;
        border: 1px solid #ddd;
        border-radius: 5px;
        background-color: #fff;
    }
    .review-form label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    .review-form input[type="number"],
    .review-form textarea {
        width: 100%;
        padding: 8px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
        border-radius: 4px;
    }
    .review-form button {
        background-color: #4CAF50;
        color: white;
        border: none;
        padding: 10px 20px;
        cursor: pointer;
        border-radius: 5px;
    }
    .review-form button:hover {
        background-color: #45a049;
    }
    .login-prompt, .error-message {
        color: red;
        font-size: 14px;
    }
    .my-review-actions {
        margin-top: 15px;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 5px;
        background-color: #f0f0f0;
    }
    .my-review-actions p {
        margin: 5px 0;
    }
    .my-review-actions button {
        margin-right: 10px;
        padding: 5px 10px;
        border: none;
        border-radius: 3px;
        cursor: pointer;
    }
    .my-review-actions button:first-child {
        background-color: #2196F3;
        color: white;
    }
    .my-review-actions button:first-child:hover {
        background-color: #1976D2;
    }
    .my-review-actions button:last-child {
        background-color: #ff4444;
        color: white;
    }
    .my-review-actions button:last-child:hover {
        background-color: #cc0000;
    }
    .reply {
        margin-top: 8px;
        padding: 8px 12px;
        background-color: #e8f5e9;
        border-left: 4px solid #4CAF50;
        color: #2e7d32;
        font-size: 13px;
        border-radius: 4px;
        font-style: italic;
    }
</style>

<script>
    function showUpdateForm(reviewId, rating, comment) {
    document.getElementById("updateReviewId").value = reviewId;
    document.getElementById("updateRating").value = rating;
    document.getElementById("updateComment").value = comment;
    document.getElementById("updateReviewForm").style.display = "block";
    }

    function deleteReview(reviewId, productId) {
    if (confirm("Are you sure you want to delete this review?")) {
    fetch('${pageContext.request.contextPath}/ReviewServlet', {
    method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'action=delete&reviewId=' + reviewId + '&productId=' + productId
    })
            .then(response => response.json())
            .then(data => {
            if (data.success) {
            alert(data.message);
            location.reload();
            } else {
            alert(data.error);
            }
            })
            .catch(error => console.error('Error:', error));
    }
    }

    document.getElementById("createReviewForm")?.addEventListener("submit", function(e) {
    e.preventDefault();
    fetch('${pageContext.request.contextPath}/ReviewServlet', {
    method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams(new FormData(this)).toString()
    })
            .then(response => response.json())
            .then(data => {
            if (data.success) {
            alert(data.message);
            location.reload();
            } else {
            alert(data.error);
            }
            })
            .catch(error => console.error('Error:', error));
    });
    document.getElementById("updateReviewForm")?.addEventListener("submit", function(e) {
    e.preventDefault();
    fetch('${pageContext.request.contextPath}/ReviewServlet', {
    method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams(new FormData(this)).toString()
    })
            .then(response => response.json())
            .then(data => {
            if (data.success) {
            alert(data.message);
            location.reload();
            } else {
            alert(data.error);
            }
            })
            .catch(error => console.error('Error:', error));
    });
</script>
