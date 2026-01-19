package io.github.nikoir.series.tracker.telegram.handler.command.impl;

import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.handler.command.Command;
import io.github.nikoir.series.tracker.telegram.model.CommandEnum;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchCommand implements Command {
    private final TelegramService telegramService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.SEARCH;
    }

    @Override
    public void execute(TelegramMessage message) {
        InlineKeyboardRow keyboardRow = new InlineKeyboardRow(List.of(InlineKeyboardButton.builder()
                .text("🔍 Поиск")
                .switchInlineQueryCurrentChat("")
                .build()));

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(keyboardRow))
                .build();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(message.chatId()))
                .text("Для поиска сериалов нажмите кнопку ниже")
                .parseMode("Markdown")
                .replyMarkup(keyboard)
                .build();
        try {
            telegramService.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception while sending search response to {}", message.userId());
            telegramService.sendErrorMessage(message.chatId());
        }
    }
}
