package io.github.nikoir.tracker.telegram.command.handler.text;

import io.github.nikoir.tracker.telegram.command.handler.base.BaseTextCommand;
import io.github.nikoir.tracker.telegram.command.enums.TextCommandEnum;
import io.github.nikoir.tracker.telegram.service.TelegramService;
import io.github.nikoir.tracker.telegram.ui.factory.MessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Component
public class SearchTextCommand extends BaseTextCommand {
    private final MessageFactory messageFactory;

    public SearchTextCommand(TelegramService telegramService,
                             MessageFactory messageFactory) {
        super(telegramService);
        this.messageFactory = messageFactory;
    }

    @Override
    public TextCommandEnum getCommand() {
        return TextCommandEnum.SEARCH;
    }

    @Override
    protected void doExecute(Message message) {
        SendMessage searchMessage = messageFactory.createSearchSendMessage(extractChatId(message));
        telegramService.execute(searchMessage);
    }
}
