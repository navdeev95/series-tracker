package io.github.nikoir.series.tracker.telegram.event.publisher;

import io.github.nikoir.series.tracker.telegram.dto.TelegramInlineQuery;
import io.github.nikoir.series.tracker.telegram.event.TelegramInlineQueryUpdateEvent;
import io.github.nikoir.series.tracker.telegram.event.TelegramMessageUpdateEvent;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

@Component
public class TelegramUpdateEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishMessageEvent(Message updateMessage) {
        TelegramMessage message = new TelegramMessage(
                updateMessage.getChatId(),
                updateMessage.getFrom().getId(),
                updateMessage.getFrom().getFirstName(),
                updateMessage.getText());

        TelegramMessageUpdateEvent updateEvent = new TelegramMessageUpdateEvent(this, message);
        applicationEventPublisher.publishEvent(updateEvent);
    }

    public void publishInlineQueryEvent(InlineQuery inlineQuery) {
        TelegramInlineQuery telegramInlineQuery = new TelegramInlineQuery(
                inlineQuery.getId(),
                inlineQuery.getFrom().getId(),
                inlineQuery.getFrom().getFirstName(),
                inlineQuery.getQuery(),
                inlineQuery.getOffset()
        );

        TelegramInlineQueryUpdateEvent updateEvent = new TelegramInlineQueryUpdateEvent(this, telegramInlineQuery);
        applicationEventPublisher.publishEvent(updateEvent);
    }
}
