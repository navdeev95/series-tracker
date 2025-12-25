package io.github.nikoir.series.tracker.telegram.dto;

public record TelegramMessage(
        Long chatId,
        Long userId,
        String userName,
        String text
) {
}
