package io.github.nikoir.seriesparser.dto.response;

public record SeriesViewDto(String title,
                            Integer year,
                            String posterUrl,
                            Integer totalSeasons) {
}
