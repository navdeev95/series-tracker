package io.github.nikoir.tracker.telegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.Optional;

import static io.github.nikoir.tracker.telegram.command.enums.TextCommandEnum.HELP;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramClient client;

    public void sendTextMessage(Long chatId, String text) {
        var sendMessage = new SendMessage(chatId.toString(), text);
        this.execute(sendMessage);
    }

    public void sendUnknownCommandMessage(Long chatId) {
        sendTextMessage(chatId, String.format("❌ Неизвестная команда. Используйте %s для списка команд", HELP.getText()));
    }

    public void sendErrorMessage(Long chatId) {
        sendTextMessage(chatId, "Произошла ошибка. Повторите запрос позже");
    }

    public <T extends Serializable> void execute(BotApiMethod<T> method){
        try {
            client.execute(method);
        } catch (TelegramApiException ex) {
            log.error("Error while sending message", ex);
        }
    }

    public Optional<Integer> execute(SendPhoto method) {
        try {
            return Optional.of(client.execute(method)).map(Message::getMessageId);
        } catch (TelegramApiException ex) {
            log.error("Error while sending message", ex);
        }
        return Optional.empty();
    }
}
