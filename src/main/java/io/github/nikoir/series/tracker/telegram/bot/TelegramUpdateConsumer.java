package io.github.nikoir.series.tracker.telegram.bot;

import io.github.nikoir.series.tracker.telegram.handler.command.CommandHandler;
import io.github.nikoir.series.tracker.telegram.handler.inline.InlineQueryHandler;
import io.github.nikoir.series.tracker.telegram.handler.message.MessageHandler;
import io.github.nikoir.series.tracker.telegram.util.CommandUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@Getter
public class TelegramUpdateConsumer implements LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {
    private final String token;
    private final CommandHandler commandHandler;
    private final InlineQueryHandler inlineQueryHandler;
    private final MessageHandler messageHandler;

    public TelegramUpdateConsumer(@Value("${telegram.bot.token}") String token,
                                  CommandHandler commandHandler,
                                  InlineQueryHandler inlineQueryHandler,
                                  MessageHandler messageHandler) {
        this.token = token;
        this.commandHandler = commandHandler;
        this.inlineQueryHandler = inlineQueryHandler;
        this.messageHandler = messageHandler;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (CommandUtil.isCommand(update.getMessage().getText())) {
                commandHandler.handle(update);
            } else {
                messageHandler.handle(update);
            }
        } else if (update.hasInlineQuery()) {
            inlineQueryHandler.handle(update);
        }
    }
}
