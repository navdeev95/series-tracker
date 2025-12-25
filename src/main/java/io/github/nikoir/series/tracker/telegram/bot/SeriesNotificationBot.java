package io.github.nikoir.series.tracker.telegram.bot;

import io.github.nikoir.series.tracker.telegram.event.publisher.TelegramUpdateEventPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@Getter
public class SeriesNotificationBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botName;
    private final String botToken;
    private final TelegramUpdateEventPublisher eventPublisher;

    public SeriesNotificationBot(String token,
                                 String botUserName,
                                 String botName,
                                 TelegramUpdateEventPublisher eventPublisher) {
        this.botToken = token;
        this.botUsername = botUserName;
        this.botName = botName;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            eventPublisher.publishMessageEvent(update.getMessage());
        }

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

    public void sendHtmlMessage(Long chatId, String htmlText) {
        sendHtmlMessage(chatId, htmlText, null);
    }

    public void sendHtmlMessage(Long chatId, String htmlText,
                                InlineKeyboardMarkup keyboard) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(htmlText)
                    .parseMode("HTML")
                    .disableWebPagePreview(true)
                    .replyMarkup(keyboard)
                    .build();

            execute(message);

        } catch (TelegramApiException e) {
            log.error("Ошибка отправки HTML сообщения", e);
        }
    }
}
