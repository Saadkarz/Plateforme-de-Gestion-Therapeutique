package com.therapist.ragchat.dto;

import java.util.List;

/**
 * Response DTO for chat endpoint
 */
public record ChatResponse(
    String answer,
    List<String> sources
) {}
