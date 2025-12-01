package com.therapist.ragchat.dto;

import java.util.List;
import java.util.Map;

/**
 * Response DTO from Python retrieval service
 */
public class RetrievalResponse {
    private List<Map<String, Object>> chunks;

    public RetrievalResponse() {}

    public RetrievalResponse(List<Map<String, Object>> chunks) {
        this.chunks = chunks;
    }

    public List<Map<String, Object>> chunks() {
        return chunks;
    }

    public void setChunks(List<Map<String, Object>> chunks) {
        this.chunks = chunks;
    }
}
