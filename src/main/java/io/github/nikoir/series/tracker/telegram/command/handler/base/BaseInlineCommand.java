package io.github.nikoir.series.tracker.telegram.command.handler.base;

import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public abstract class BaseInlineCommand extends BaseCommand<InlineCommandEnum> {
    @Override
    public void execute(Update update) {
        InlineQuery inlineQuery = update.getInlineQuery();
        this.innerExecute(inlineQuery);

    }

    protected abstract void innerExecute(InlineQuery inlineQuery);
}
