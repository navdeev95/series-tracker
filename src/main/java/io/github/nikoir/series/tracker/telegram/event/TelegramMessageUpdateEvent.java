package io.github.nikoir.series.tracker.telegram.event;

import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TelegramMessageUpdateEvent extends ApplicationEvent {
    private final TelegramMessage telegramMessage;

    public TelegramMessageUpdateEvent(Object source, TelegramMessage telegramMessage) {
        super(source);
        this.telegramMessage = telegramMessage;
    }
}
