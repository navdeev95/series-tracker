package io.github.nikoir.series.tracker.telegram.event.publisher;

import io.github.nikoir.series.tracker.telegram.event.TelegramUpdateEvent;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

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

        TelegramUpdateEvent updateEvent = new TelegramUpdateEvent(this, message);
        applicationEventPublisher.publishEvent(updateEvent);
    }
}
