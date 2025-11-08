package io.github.nikoir.seriesparser.dto.request;

public record SeriesSearchRq(String title,
                             int page,
                             int limit) {
}
