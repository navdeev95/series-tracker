package io.github.nikoir.tracker.telegram.bot;

import io.github.nikoir.tracker.telegram.bot.dispatcher.UpdateDispatcher;
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
    private final UpdateDispatcher updateDispatcher;

    public TelegramUpdateConsumer(@Value("${telegram.bot.token}") String token,
                                  UpdateDispatcher updateDispatcher) {
        this.token = token;
        this.updateDispatcher = updateDispatcher;
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
        updateDispatcher.dispatch(update);
    }
}
