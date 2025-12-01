package com.therapist.ragchat.service;

import com.therapist.ragchat.client.OllamaClient;
import com.therapist.ragchat.client.RetrievalClient;
import com.therapist.ragchat.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Service that implements the RAG pipeline with crisis detection
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RetrievalClient retrievalClient;
    private final OllamaClient ollamaClient;

    // Crisis keywords for detection
    private static final List<String> CRISIS_KEYWORDS = List.of(
            "suicide", "suicider", "me tuer", "me faire du mal",
            "je veux mourir", "envie de mourir", "mettre fin",
            "en finir", "me suicider"
    );

    private static final String CRISIS_RESPONSE =
            "⚠️ Si vous êtes en danger ou envisagez de vous faire du mal, " +
            "contactez immédiatement les services d'urgence (15 en France, 112 en Europe) " +
            "ou une ligne d'écoute comme SOS Amitié (09 72 39 40 50). " +
            "Votre vie est précieuse et il existe des personnes prêtes à vous aider.";

    private static final String SYSTEM_PROMPT =
            """
            Tu es un thérapeute bienveillant et empathique. Réponds en français de manière concise et structurée.
            Structure ta réponse en 4 phrases maximum :
            1. Une phrase d'empathie pour reconnaître les émotions exprimées
            2. Deux actions concrètes et réalisables immédiatement
            3. Une recommandation utile pour aller plus loin
            
            Si la situation contient un risque pour la personne, conseille immédiatement d'appeler les secours ou une ligne d'écoute.
            Reste toujours bienveillant, sans jugement, et orienté vers des solutions pratiques.
            """;

    /**
     * Processes a chat request through the RAG pipeline
     *
     * @param question user's question
     * @return chat response with answer and sources
     */
    public Mono<ChatResponse> processChat(String question) {
        log.info("Processing chat request: {}", question);

        // Step 1: Crisis detection
        if (detectCrisis(question)) {
            log.warn("Crisis keywords detected in question");
            return Mono.just(new ChatResponse(CRISIS_RESPONSE, List.of("CRISIS_DETECTION")));
        }

        // Step 2: Retrieve relevant chunks
        return retrievalClient.retrieve(question, 5)
                .flatMap(chunks -> {
                    log.debug("Retrieved {} chunks for context", chunks.size());

                    // Step 3: Build context from chunks
                    String context = buildContext(chunks);
                    List<String> sources = extractSources(chunks);

                    // Step 4: Build user message with context
                    String userMessage = buildUserMessage(context, question);

                    // Step 5: Call Ollama for completion
                    return ollamaClient.chatCompletion(SYSTEM_PROMPT, userMessage)
                            .map(answer -> new ChatResponse(answer, sources));
                })
                .onErrorResume(error -> {
                    log.error("Error in chat processing: {}", error.getMessage(), error);
                    return Mono.just(new ChatResponse(
                            "Désolé, une erreur s'est produite. Veuillez réessayer.",
                            List.of()
                    ));
                });
    }

    /**
     * Detects crisis-related keywords in the question
     */
    private boolean detectCrisis(String question) {
        String lowerQuestion = question.toLowerCase();
        return CRISIS_KEYWORDS.stream()
                .anyMatch(lowerQuestion::contains);
    }

    /**
     * Builds context string from retrieved chunks
     */
    private String buildContext(List<Map<String, Object>> chunks) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> chunk = chunks.get(i);
            String text = (String) chunk.getOrDefault("text", "");
            context.append("--- Extrait ").append(i + 1).append(" ---\n");
            context.append(text).append("\n\n");
        }
        return context.toString();
    }

    /**
     * Extracts unique source filenames from chunks
     */
    private List<String> extractSources(List<Map<String, Object>> chunks) {
        return chunks.stream()
                .map(chunk -> (String) chunk.get("source"))
                .filter(Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Builds the final user message with context and question
     */
    private String buildUserMessage(String context, String question) {
        return "Contexte issu de documents thérapeutiques :\n\n" +
                context.trim() + "\n\n" +
                "Question de l'utilisateur : " + question + "\n\n" +
                "Réponds en te basant sur le contexte fourni et ton expertise thérapeutique.";
    }
}
