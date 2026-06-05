package io.github.nikoir.series.tracker.telegram.command.handler.text;

import io.github.nikoir.series.tracker.telegram.command.enums.TextCommandEnum;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseTextCommand;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.ui.factory.MessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static io.github.nikoir.series.tracker.telegram.command.enums.TextCommandEnum.MY_SUBSCRIPTIONS;

@Component
public class SubscriptionsTextCommand extends BaseTextCommand {
    private final MessageFactory messageFactory;
    public SubscriptionsTextCommand(TelegramService telegramService,
                                    MessageFactory messageFactory) {
        super(telegramService);
        this.messageFactory = messageFactory;
    }

    @Override
    public TextCommandEnum getCommand() {
        return MY_SUBSCRIPTIONS;
    }
    @Override
    protected void doExecute(Message message) {
        SendMessage sendMessage = messageFactory.createSubscriptionsSendMessage(extractChatId(message));
        telegramService.execute(sendMessage);
    }
}
