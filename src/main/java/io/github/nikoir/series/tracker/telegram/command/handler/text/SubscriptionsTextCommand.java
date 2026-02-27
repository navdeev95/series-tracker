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

@RequiredArgsConstructor
@Component
public class SubscriptionsTextCommand extends BaseTextCommand {
    private final TelegramService telegramService;
    private final MessageFactory messageFactory;

    @Override
    public TextCommandEnum getCommand() {
        return MY_SUBSCRIPTIONS;
    }
    @Override
    protected void innerExecute(Message message) {
        SendMessage sendMessage = messageFactory.createSubscriptionsSendMessage(message.getFrom().getId());
        telegramService.execute(sendMessage);
    }
}
