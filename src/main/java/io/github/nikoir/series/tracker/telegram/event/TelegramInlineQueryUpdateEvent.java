package io.github.nikoir.series.tracker.telegram.event;

import io.github.nikoir.series.tracker.telegram.dto.TelegramInlineQuery;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TelegramInlineQueryUpdateEvent extends ApplicationEvent {
    private final TelegramInlineQuery inlineQuery;

    public TelegramInlineQueryUpdateEvent(Object source, TelegramInlineQuery inlineQuery) {
        super(source);
        this.inlineQuery = inlineQuery;
    }
}
