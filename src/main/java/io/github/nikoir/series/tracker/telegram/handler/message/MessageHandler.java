package io.github.nikoir.series.tracker.telegram.handler.message;

import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.handler.BaseHandler;
import io.github.nikoir.series.tracker.telegram.handler.command.Command;
import io.github.nikoir.series.tracker.telegram.handler.command.CommandRegistry;
import io.github.nikoir.series.tracker.telegram.model.ButtonEnum;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler extends BaseHandler {
    private final CommandRegistry commandRegistry;
    private final TelegramService telegramService;

    @Override
    public void handle(Update update) {
        String text = update.getMessage().getText();

        Optional<ButtonEnum> foundButton = ButtonEnum.fromDisplayText(text);
        foundButton.ifPresentOrElse(button -> handleButton(update, button),
                () -> telegramService.sendUnknownCommandMessage(update.getMessage().getChatId()));
    }

    private void handleButton(Update update, ButtonEnum button) {
        String commandText = button.getCommand().getCommandText();
        Optional<Command> foundCommand = commandRegistry.getCommand(commandText);

        TelegramMessage message = TelegramMessage.fromTelegramUpdate(update);
        foundCommand.ifPresentOrElse(command -> command.execute(message),
                () -> telegramService.sendUnknownCommandMessage(message.chatId()));
    }
}
