package io.github.nikoir.series.tracker.telegram.handler.impl;

import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.handler.Command;
import io.github.nikoir.series.tracker.telegram.model.BotCommandEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchCommand implements Command {
    private final SeriesNotificationBot bot;

    @Override
    public BotCommandEnum getCommand() {
        return BotCommandEnum.SEARCH;
    }

    @Override
    public void execute(TelegramMessage message) {
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(List.of(InlineKeyboardButton.builder()
                        .text("🔍 Поиск")
                        .switchInlineQueryCurrentChat("")
                        .build())))
                .build();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(message.chatId()))
                .text("Для поиска сериалов нажмите кнопку ниже")
                .parseMode("Markdown")
                .replyMarkup(keyboard)
                .build();
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception while sending search response to {}", message.userId());
            bot.sendTextMessage(message.chatId(), "Произошла ошибка. Повторите запрос позже.");
        }
    }
}
