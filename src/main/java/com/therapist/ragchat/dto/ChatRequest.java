package com.therapist.ragchat.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for chat endpoint
 */
public record ChatRequest(
    @NotBlank(message = "Question cannot be blank")
    String question
) {}
