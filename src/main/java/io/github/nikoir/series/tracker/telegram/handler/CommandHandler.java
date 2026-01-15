package io.github.nikoir.series.tracker.telegram.handler;

import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.strategy.context.SeriesSearchStrategyContext;
import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.dto.TelegramInlineQuery;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.event.TelegramInlineQueryUpdateEvent;
import io.github.nikoir.series.tracker.telegram.event.TelegramMessageUpdateEvent;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.util.CommandUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final CommandRegistry commandRegistry;
    private final SeriesSearchStrategyContext searchStrategyContext;
    private final SeriesNotificationBot bot;
    private final SeriesSendService seriesSendService;

    @EventListener
    public void handleMessage(TelegramMessageUpdateEvent updateEvent) {
        TelegramMessage message = updateEvent.getTelegramMessage();

        String text = message.text();
        Long userId = message.userId();
        Long chatId = message.chatId();

        log.info("Message from {} ({}): {}", userId, chatId, text);

        // Проверяем состояние пользователя (для многошаговых операций)
        /*UserStateEnum userState = userSessionService.getUserState(userId);
        if (userState != null) {
            handleUserState(userState, message);
            return;
        }*/

        // Проверяем команду
        if (CommandUtil.isCommand(text)) {
            handleCommand(message);
        } else {
            handleUnknownMessage(message);
        }
    }

    @EventListener
    public void handleInlineQuery(TelegramInlineQueryUpdateEvent updateEvent) {
        PagedModel<SeriesShortViewRs> series = findSeries(updateEvent.getInlineQuery());
        seriesSendService.sendSeriesInline(updateEvent.getInlineQuery().queryId(), series);
    }

    private PagedModel<SeriesShortViewRs> findSeries(TelegramInlineQuery inlineQuery) {
        String query = inlineQuery.query().toLowerCase();
        String offset = inlineQuery.offset();
        int page = 0;

        // Парсим offset для определения страницы
        if (offset != null && !offset.isEmpty()) {
            try {
                page = Integer.parseInt(offset);
            } catch (NumberFormatException ignored) {
            }
        }

        int pageSize = 10;

        return searchStrategyContext.search(new SeriesSearchRq(query, page, pageSize));
    }

    private void handleCommand(TelegramMessage message) {
        String text = message.text();
        String commandName = CommandUtil.extractCommand(text);

        Command command = commandRegistry.getCommand(commandName);
        if (command != null) {
            command.execute(message);
        } else {
            bot.sendTextMessage(message.chatId(),
                    "❌ Неизвестная команда. Используйте /help для списка команд");
        }
    }

    private void handleUnknownMessage(TelegramMessage message) {
        bot.sendTextMessage(message.chatId(),
                """
                        Ты совсем долбоеб? 😡
                        Ты командами общайся, блять.
                        Я тебе что, нахуй, ChatGPT что ли?🤦‍♂️
                        Мой создатель блять в МНУ работает. В МНУ, нахуй, а не в OpenAI!
                        Тут блять половина кода DeepSeek-ом сгенерирована.
                        Дебила кусок...
                        /help тебе в помощь, уебище""");
    }

    private void sendErrorMessage(Update update, SeriesNotificationBot bot, Exception e) {
        try {
            Long chatId = getChatId(update);
            bot.sendTextMessage(chatId,
                    "⚠️ Произошла ошибка при обработке запроса. Попробуйте позже.");
            log.error("Ошибка в чате {}: {}", chatId, e.getMessage());
        } catch (Exception ex) {
            log.error("Не удалось отправить сообщение об ошибке", ex);
        }
    }

    private Long getChatId(Update update) {
        if (update.hasMessage()) return update.getMessage().getChatId();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getMessage().getChatId();
        return null;
    }
}