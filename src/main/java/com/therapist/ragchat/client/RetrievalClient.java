package com.therapist.ragchat.client;

import com.therapist.ragchat.dto.RetrievalRequest;
import com.therapist.ragchat.dto.RetrievalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Client to communicate with Python retrieval microservice
 */
@Slf4j
@Component
public class RetrievalClient {

    private final WebClient webClient;
    private final Duration timeout;

    public RetrievalClient(
            @Value("${app.retrieval.url}") String retrievalUrl,
            @Value("${app.retrieval.timeout:30000}") long timeoutMs,
            WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(retrievalUrl)
                .build();
        this.timeout = Duration.ofMillis(timeoutMs);
        log.info("RetrievalClient initialized with URL: {}", retrievalUrl);
    }

    /**
     * Retrieves relevant document chunks from Python microservice
     *
     * @param query user query
     * @param topK  number of chunks to retrieve
     * @return list of chunks as maps containing text, source, chunk_id
     */
    public Mono<List<Map<String, Object>>> retrieve(String query, int topK) {
        log.debug("Retrieving chunks for query: '{}' with top_k={}", query, topK);
        
        RetrievalRequest request = new RetrievalRequest(query, topK);

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RetrievalResponse.class)
                .timeout(timeout)
                .map(RetrievalResponse::chunks)
                .doOnSuccess(chunks -> log.debug("Retrieved {} chunks", chunks != null ? chunks.size() : 0))
                .doOnError(error -> log.error("Error retrieving chunks: {}", error.getMessage()));
    }
}
