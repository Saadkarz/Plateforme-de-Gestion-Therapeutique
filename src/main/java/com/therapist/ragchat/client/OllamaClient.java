package com.therapist.ragchat.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Client to interact with Ollama API for local LLM inference
 */
@Slf4j
@Component
public class OllamaClient {

    private final WebClient webClient;
    private final String model;
    private final ObjectMapper objectMapper;

    public OllamaClient(
            @Value("${app.ollama.endpoint:http://localhost:11434}") String endpoint,
            @Value("${app.ollama.model:llama3.1}") String model,
            WebClient.Builder webClientBuilder) {
        this.model = model;
        this.objectMapper = new ObjectMapper();
        this.webClient = webClientBuilder
                .baseUrl(endpoint)
                .build();
        
        log.info("OllamaClient initialized with model: {} at {}", model, endpoint);
    }

    public Mono<String> chatCompletion(String systemPrompt, String userMessage) {
        log.debug("Calling Ollama API for text generation");

        // Format prompt pour Llama 3.1
        String prompt = String.format("""
            <|begin_of_text|><|start_header_id|>system<|end_header_id|>
            
            %s<|eot_id|><|start_header_id|>user<|end_header_id|>
            
            %s<|eot_id|><|start_header_id|>assistant<|end_header_id|>
            """, systemPrompt, userMessage);

        OllamaRequest request = new OllamaRequest(model, prompt, false);

        return webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(120))
                .map(this::parseResponse)
                .doOnError(error -> log.error("Error calling Ollama API: {}", error.getMessage()))
                .onErrorReturn("Je m'excuse, je rencontre des difficultés techniques. Veuillez réessayer dans un moment.");
    }

    private String parseResponse(String jsonResponse) {
        try {
            StringBuilder fullResponse = new StringBuilder();
            String[] lines = jsonResponse.split("\n");
            
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Map<String, Object> jsonLine = objectMapper.readValue(line, Map.class);
                    String responseText = (String) jsonLine.get("response");
                    if (responseText != null) {
                        fullResponse.append(responseText);
                    }
                }
            }
            
            String result = fullResponse.toString().trim();
            log.debug("Ollama response parsed: {} characters", result.length());
            return result.isEmpty() ? "Désolé, je n'ai pas pu générer de réponse." : result;
            
        } catch (JsonProcessingException e) {
            log.error("Error parsing Ollama response: {}", e.getMessage());
            return "Erreur lors du traitement de la réponse.";
        }
    }

    /**
     * Request object for Ollama API
     */
    record OllamaRequest(
            @JsonProperty("model") String model,
            @JsonProperty("prompt") String prompt,
            @JsonProperty("stream") boolean stream
    ) {}
}
