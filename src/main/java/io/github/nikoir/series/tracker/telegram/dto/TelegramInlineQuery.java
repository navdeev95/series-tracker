package io.github.nikoir.series.tracker.telegram.dto;

public record TelegramInlineQuery(
        String queryId,
        Long userId,
        String userName,
        String query,
        String offset
) {
}
