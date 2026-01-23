package io.github.nikoir.series.tracker.telegram.handler.command;

import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.model.CommandEnum;

public interface Command {
    CommandEnum getCommand();
    void execute(TelegramMessage message);
}
