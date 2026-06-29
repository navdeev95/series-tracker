package io.github.nikoir.series.tracker.telegram.command.handler.base;

import io.github.nikoir.series.tracker.telegram.command.enums.TextCommandEnum;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public abstract class BaseTextCommand extends BaseCommand<TextCommandEnum, Message> {
    public BaseTextCommand(TelegramService telegramService) {
        super(telegramService);
    }

    @Override
    protected Message extractRequest(Update update) {
        return update.getMessage();
    }

    @Override
    protected User extractUser(Message request) {
        return request.getFrom();
    }
}
