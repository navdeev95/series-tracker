package io.github.nikoir.series.tracker.telegram.dto;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public record TelegramMessage(
        Long chatId,
        Long userId,
        String userName,
        String text
) {
    public static TelegramMessage fromTelegramUpdate(Update update) {
        Message message = update.getMessage();
        User sender = update.getMessage().getFrom();
        return new TelegramMessage(message.getChatId(),
                sender.getId(),
                sender.getFirstName(),
                message.getText());
    }
}
