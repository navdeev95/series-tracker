package io.github.nikoir.series.tracker.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BaseHandler {
    public abstract void handle(Update update);
}
