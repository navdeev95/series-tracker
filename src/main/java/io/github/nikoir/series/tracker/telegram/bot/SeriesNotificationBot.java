package io.github.nikoir.series.tracker.telegram.bot;

import io.github.nikoir.series.tracker.telegram.handler.CommandHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@Getter
public class SeriesNotificationBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botName;
    private final String botToken;
    private final CommandHandler commandHandler;

    public SeriesNotificationBot(CommandHandler commandHandler,
                                 String token,
                                 String botUserName,
                                 String botName) {
        this.commandHandler = commandHandler;
        this.botToken = token;
        this.botUsername = botUserName;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        commandHandler.handleUpdate(update, this);
    }

    public void sendTextMessage(Long chatId, String text) {
        try {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(text);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в чат {}", chatId, e);
        }
    }
}
