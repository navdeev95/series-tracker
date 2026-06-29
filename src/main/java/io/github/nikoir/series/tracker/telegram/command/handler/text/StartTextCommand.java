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
@Component
public class StartTextCommand extends BaseTextCommand {
    private final MessageFactory messageFactory;

    public StartTextCommand(TelegramService telegramService,
                            MessageFactory messageFactory) {
        super(telegramService);
        this.messageFactory = messageFactory;
    }

    @Override
    public TextCommandEnum getCommand() {
        return TextCommandEnum.START;
    }

    @Override
    protected void doExecute(Message message) {
        User user = extractUser(message);

        log.info("User {} ({}) has run bot", extractChatId(message), user.getFirstName());

        SendMessage response = messageFactory.createWelcomeMessage(user);

        telegramService.execute(response);
    }
}
