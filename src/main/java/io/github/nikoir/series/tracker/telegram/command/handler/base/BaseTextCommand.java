package io.github.nikoir.series.tracker.telegram.command.handler.base;

import io.github.nikoir.series.tracker.telegram.command.enums.TextCommandEnum;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public abstract class BaseTextCommand extends BaseCommand<TextCommandEnum> {
    @Override
    public void execute(Update update) {
        Message updateMessage = update.getMessage();
        this.innerExecute(updateMessage);
    }

    protected abstract void innerExecute(Message message);
}
