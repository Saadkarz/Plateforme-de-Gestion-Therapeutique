package com.therapist.ragchat.controller;

import com.therapist.ragchat.dto.ChatRequest;
import com.therapist.ragchat.dto.ChatResponse;
import com.therapist.ragchat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller for chat endpoint
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * Chat endpoint that processes user questions through RAG pipeline
     *
     * @param request chat request with user question
     * @return chat response with answer and sources
     */
    @PostMapping("/chat")
    public Mono<ResponseEntity<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat request for question: {}", request.question());

        return chatService.processChat(request.question())
                .map(response -> {
                    log.info("Chat response generated with {} sources", response.sources().size());
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("Error processing chat request: {}", error.getMessage(), error);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ChatResponse(
                                    "Une erreur s'est produite lors du traitement de votre demande.",
                                    java.util.List.of()
                            ))
                    );
                });
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
