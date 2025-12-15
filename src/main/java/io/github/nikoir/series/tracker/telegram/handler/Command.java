package io.github.nikoir.series.tracker.telegram.handler;

import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.model.BotCommandEnum;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Command {
    BotCommandEnum getCommand();
    void execute(Message message, SeriesNotificationBot bot);
}
