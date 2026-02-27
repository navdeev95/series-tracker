package io.github.nikoir.series.tracker.telegram.command.handler.text;

import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseTextCommand;
import io.github.nikoir.series.tracker.telegram.command.enums.TextCommandEnum;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.ui.factory.MessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@RequiredArgsConstructor
@Component
public class StartTextCommand extends BaseTextCommand {
    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public TextCommandEnum getCommand() {
        return TextCommandEnum.START;
    }

    @Override
    protected void innerExecute(Message message) {
        User user = message.getFrom();

        log.info("User {} ({}) has run bot", user.getId(), user.getFirstName());

        SendMessage response = messageFactory.createWelcomeMessage(user);

        telegramService.execute(response);
    }
}
