package io.github.nikoir.series.tracker.telegram.handler.command;

import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.handler.BaseHandler;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.util.CommandUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler extends BaseHandler {
    private final CommandRegistry commandRegistry;
    private final TelegramService telegramService;

    @Override
    public void handle(Update update) {
        Message updateMessage = update.getMessage();
        User sender = update.getMessage().getFrom();

        TelegramMessage message = new TelegramMessage(updateMessage.getChatId(),
                sender.getId(),
                sender.getFirstName(),
                updateMessage.getText());

        String commandName = CommandUtil.extractCommand(message.text());
        Optional<Command> foundCommand = commandRegistry.getCommand(commandName);

        foundCommand.ifPresentOrElse(command -> command.execute(message),
                () -> telegramService.sendUnknownCommandMessage(message.chatId()));

    }
}