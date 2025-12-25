package io.github.nikoir.series.tracker.telegram.handler;

import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.model.BotCommandEnum;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Command {
    BotCommandEnum getCommand();
    void execute(TelegramMessage message);
}
