package io.github.nikoir.series.tracker.telegram.command.handler.text;

import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseTextCommand;
import io.github.nikoir.series.tracker.telegram.command.enums.TextCommandEnum;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.ui.factory.MessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

import static io.github.nikoir.series.tracker.telegram.command.util.CommandUtil.createCommandString;

@Slf4j
@RequiredArgsConstructor
@Component
public class SearchTextCommand extends BaseTextCommand {
    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public TextCommandEnum getCommand() {
        return TextCommandEnum.SEARCH;
    }

    @Override
    protected void innerExecute(Message message) {
        SendMessage searchMessage = messageFactory.createSearchSendMessage(message.getChatId());
        telegramService.execute(searchMessage);
    }
}
