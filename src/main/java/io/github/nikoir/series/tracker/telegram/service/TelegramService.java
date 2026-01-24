package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.telegram.model.CommandEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

import static io.github.nikoir.series.tracker.telegram.model.CommandEnum.HELP;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramClient client;

    public void sendTextMessage(Long chatId, String text) {
        try {
            var sendMessage = new SendMessage(chatId.toString(), text);
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message to chat {}", chatId, e);
        }
    }

    public void sendUnknownCommandMessage(Long chatId) {
        sendTextMessage(chatId, String.format("❌ Неизвестная команда. Используйте %s для списка команд", HELP.getCommandText()));
    }

    public void sendErrorMessage(Long chatId) {
        sendTextMessage(chatId, "Произошла ошибка. Повторите запрос позже");
    }

    public <T extends Serializable> void execute(BotApiMethod<T> method) throws TelegramApiException {
        client.execute(method);
    }

    public void execute(SendPhoto method) throws TelegramApiException {
        client.execute(method);
    }

}
