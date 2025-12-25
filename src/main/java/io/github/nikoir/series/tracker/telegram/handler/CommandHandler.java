package io.github.nikoir.series.tracker.telegram.handler;

import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.event.TelegramUpdateEvent;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import io.github.nikoir.series.tracker.telegram.model.UserStateEnum;
import io.github.nikoir.series.tracker.telegram.util.CommandUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;

import static io.github.nikoir.series.tracker.telegram.model.BotCommandEnum.SEARCH;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final CommandRegistry commandRegistry;
    //private final CallbackQueryHandler callbackHandler;
    private final UserSessionService userSessionService;
    private final SeriesNotificationBot bot;

    @EventListener
    public void handleMessage(TelegramUpdateEvent updateEvent) {
        TelegramMessage message = updateEvent.getTelegramMessage();

        String text = message.text();
        Long userId = message.userId();
        Long chatId = message.chatId();

        log.info("Message from {} ({}): {}", userId, chatId, text);

        // Проверяем состояние пользователя (для многошаговых операций)
        UserStateEnum userState = userSessionService.getUserState(userId);
        if (userState != null) {
            handleUserState(userState, message);
            return;
        }

        // Проверяем команду
        if (CommandUtil.isCommand(text)) {
            handleCommand(message);
        } else {
            handleUnknownMessage(message);
        }
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

    private void handleUserState(UserStateEnum state, TelegramMessage message) {
        if (Objects.requireNonNull(state) == UserStateEnum.AWAITING_SEARCH_QUERY) {
            commandRegistry.getCommand(SEARCH.getCommandText())
                    .execute(message);
        } else {
            bot.sendTextMessage(message.chatId(), "Произошла ошибка. Пожалуйста, начните заново.");
            userSessionService.clearUserState(message.userId());
        }
    }

    private void handleInlineQuery(InlineQuery inlineQuery, SeriesNotificationBot bot) {
        // Отправляем ответ
        try {
            AnswerInlineQuery answer = new AnswerInlineQuery();
            answer.setInlineQueryId(inlineQuery.getId());
            answer.setResults(List.of(createInlineDisabledResult(bot)));
            answer.setCacheTime(1); // Кешируем на 1 секунду
            answer.setIsPersonal(true);
            answer.setSwitchPmText("Открыть бота для поиска");
            answer.setSwitchPmParameter("inline_disabled");

            bot.execute(answer);
            log.debug("Отправлен ответ на inline-запрос (режим отключен)");

        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке inline-ответа", e);
        }
    }

    private InlineQueryResultArticle createInlineDisabledResult(SeriesNotificationBot bot) {
        InlineQueryResultArticle article = new InlineQueryResultArticle();
        article.setId("go_to_bot");

        article.setTitle(String.format("🚀 Перейти в %s", bot.getBotName()));
        article.setDescription("Нажмите, чтобы открыть бота для поиска сериалов");

        // Сообщение с кнопкой для перехода
        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.setMessageText(String.format("Чтобы найти сериалы и получать уведомления о новых сериях, " +
                "перейдите в %s 👇", bot.getBotUsername()));
        article.setInputMessageContent(messageContent);

        // Кнопка для быстрого запуска бота
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(String.format("🔍 Открыть %s", bot.getBotName()));
        button.setUrl(String.format("https://t.me/%s?start=from_inline",
                bot.getBotUsername().replace("@", "")));

        keyboard.setKeyboard(List.of(List.of(button)));
        article.setReplyMarkup(keyboard);

        return article;
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