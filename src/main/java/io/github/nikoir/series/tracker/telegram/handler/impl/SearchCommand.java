package io.github.nikoir.series.tracker.telegram.handler.impl;

import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.handler.Command;
import io.github.nikoir.series.tracker.telegram.model.BotCommandEnum;
import org.telegram.telegrambots.meta.api.objects.Message;

public class SearchCommand implements Command {
    @Override
    public BotCommandEnum getCommand() {
        return BotCommandEnum.SEARCH;
    }

    @Override
    public void execute(Message message, SeriesNotificationBot bot) {

    }
}
