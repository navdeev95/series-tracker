package io.github.nikoir.series.tracker.content.config.logging;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request,
                                        byte @NonNull [] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response, request);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("=== HTTP REQUEST ===");
        log.info("URI: {}", request.getURI());
        log.info("Method: {}", request.getMethod());
        log.info("Headers: {}", request.getHeaders());
        log.info("Body: {}", new String(body, StandardCharsets.UTF_8));
        log.info("====================");
    }

    private void logResponse(ClientHttpResponse response, HttpRequest request) throws IOException {
        log.info("=== HTTP RESPONSE ===");
        log.info("URI: {}", request.getURI());
        log.info("Status: {} {}", response.getStatusCode(), response.getStatusText());
        log.info("Headers: {}", response.getHeaders());
        log.info("Body: {}", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
        log.info("=====================");
    }
}
