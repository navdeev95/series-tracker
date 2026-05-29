package io.github.nikoir.series.tracker.telegram.command.handler.base;

import io.github.nikoir.series.tracker.telegram.command.enums.CommandEnum;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BaseCommand<T extends CommandEnum> {
    public abstract T getCommand();
    public abstract void execute(Update update);
}
