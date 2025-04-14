package com.mvc.model;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private int productId;
    private int customerId;
    private int rating;
    private String comment;
    private String reply;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp replyAt;

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Timestamp getReplyAt() { return replyAt; }
    public void setReplyAt(Timestamp replyAt) { this.replyAt = replyAt; }
}
