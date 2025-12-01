package com.therapist.ragchat.dto;

/**
 * Request DTO for Python retrieval service
 */
public class RetrievalRequest {
    private String query;
    private int top_k;

    public RetrievalRequest() {}

    public RetrievalRequest(String query, int top_k) {
        this.query = query;
        this.top_k = top_k;
    }

    public String getQuery() {
        return query;
    }

    public int getTop_k() {
        return top_k;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setTop_k(int top_k) {
        this.top_k = top_k;
    }
}
