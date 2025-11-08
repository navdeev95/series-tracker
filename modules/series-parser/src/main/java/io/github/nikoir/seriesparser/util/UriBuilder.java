package io.github.nikoir.seriesparser.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

public final class UriBuilder {
    private final String baseUrl;
    private final Map<String, Object> params = new LinkedHashMap<>();
    private final List<String> paths = new ArrayList<>();

    private UriBuilder(String baseUrl) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "Base URL cannot be null");
    }

    public static UriBuilder from(String baseUrl) {
        return new UriBuilder(baseUrl);
    }

    public UriBuilder path(String path) {
        if (isNotBlank(path)) {
            this.paths.add(path);
        }
        return this;
    }

    public UriBuilder paths(String... paths) {
        if (paths != null) {
            Collections.addAll(this.paths, paths);
        }
        return this;
    }

    public UriBuilder param(String key, Object value) {
        if (isNotBlank(key) && value != null) {
            this.params.put(key, value);
        }
        return this;
    }

    public UriBuilder params(Map<String, Object> params) {
        if (params != null) {
            this.params.putAll(params);
        }
        return this;
    }

    public String build() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);

        // Добавляем пути
        paths.stream()
                .filter(Objects::nonNull)
                .filter(this::isNotBlank)
                .forEach(builder::pathSegment);

        // Добавляем параметры
        params.forEach((key, value) -> {
            if (isNotBlank(key) && value != null) {
                builder.queryParam(key, value.toString());
            }
        });

        return builder.encode().build().toUriString();
    }

    public URI buildUri() {
        return URI.create(build());
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}