package io.github.nikoir.tracker.telegram.bot.dispatcher;

import io.github.nikoir.tracker.telegram.command.enums.CallbackCommandEnum;
import io.github.nikoir.tracker.telegram.command.enums.CommandEnum;
import io.github.nikoir.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.tracker.telegram.command.enums.TextCommandEnum;
import io.github.nikoir.tracker.telegram.command.handler.base.BaseCommand;
import io.github.nikoir.tracker.telegram.ui.CommandButtonEnum;
import io.github.nikoir.tracker.telegram.service.TelegramService;
import io.github.nikoir.tracker.telegram.command.util.CommandUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class UpdateDispatcher {
    private final List<BaseCommand<?, ?>> commands;
    private final TelegramService telegramService;

    public void dispatch(Update update) {
        if (canSkipUpdate(update)) {
            return;
        }
        Optional<BaseCommand<?, ?>> foundCommand = findCommand(update);
        foundCommand.ifPresentOrElse(
                command -> command.execute(update),
                () -> handleUnknownCommand(update));
    }

    private boolean canSkipUpdate(Update update) {
        return isMessageViaBot(update) || isEmptyCallback(update);
    }

    private boolean isMessageViaBot(Update update) {
        return update.hasMessage() && update.getMessage().hasViaBot();
    }

    private boolean isEmptyCallback(Update update) {
        return update.hasCallbackQuery() && StringUtils.isBlank(update.getCallbackQuery().getData());
    }

    private Optional<BaseCommand<?, ?>> findCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Optional<TextCommandEnum> textCommandEnum;
            if (CommandUtil.isTextCommand(messageText)) {
                textCommandEnum = getTextCommandEnumFromCommandMessage(messageText);
            }
            else {
                textCommandEnum = getTextCommandEnumFromTextMessage(messageText);
            }
            return textCommandEnum.flatMap(this::getCommand);
        } else if (update.hasInlineQuery()) {
            Optional<InlineCommandEnum> inlineCommandEnum = getInlineCommandEnum(update);
            return inlineCommandEnum.flatMap(this::getCommand);
        } else if (update.hasCallbackQuery()) {
            Optional<CallbackCommandEnum> callbackCommandEnum = getCallbackCommandEnum(update);
            return callbackCommandEnum.flatMap(this::getCommand);
        }
        return Optional.empty();
    }

    private Optional<TextCommandEnum> getTextCommandEnumFromCommandMessage(String commandMessage) {
        String commandText = CommandUtil.extractCommandText(TextCommandEnum.class, commandMessage);
        return CommandUtil.fromCommandText(TextCommandEnum.class, commandText);
    }

    private Optional<TextCommandEnum> getTextCommandEnumFromTextMessage(String textMessage) {
        Optional<CommandButtonEnum> button = CommandButtonEnum.fromDisplayText(textMessage);
        return button.map(CommandButtonEnum::getCommand);
    }

    private Optional<InlineCommandEnum> getInlineCommandEnum(Update update) {
        String commandText = CommandUtil.extractCommandText(InlineCommandEnum.class, update.getInlineQuery().getQuery());
        return CommandUtil.fromCommandText(InlineCommandEnum.class, commandText);
    }

    private Optional<CallbackCommandEnum> getCallbackCommandEnum(Update update) {
        String commandText = CommandUtil.extractCommandText(CallbackCommandEnum.class,
                update.getCallbackQuery().getData());
        return CommandUtil.fromCommandText(CallbackCommandEnum.class, commandText);
    }

    private <T extends Enum<T> & CommandEnum> Optional<BaseCommand<?, ?>> getCommand(T commandEnum) {
        return commands.stream()
                .filter(c -> c.getCommand() == commandEnum)
                .findFirst();
    }

    private void handleUnknownCommand(Update update) {
        if (update.hasMessage()) {
            telegramService.sendUnknownCommandMessage(update.getMessage().getChatId());
        } else if (update.hasCallbackQuery()) {
            telegramService.sendErrorMessage(update.getCallbackQuery().getFrom().getId());
        }
    }
}
