package io.github.nikoir.series.tracker.telegram.bot.config;

import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.handler.CommandHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Getter
@Configuration
@Slf4j
public class TelegramBotConfig {
    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.name}")
    private String name;

    @Value("${telegram.bot.username}")
    private String userName;

    @Value("${telegram.bot.enabled:true}")
    private boolean enabled;

    @Bean
    public SeriesNotificationBot seriesNotificationBot(CommandHandler commandHandler) {
        log.info("Создание бота с username: {}", userName);
        return new SeriesNotificationBot(commandHandler, token, userName, name);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(SeriesNotificationBot bot) {
        if (!enabled) {
            log.warn("Telegram бот отключен в конфигурации");
            return null;
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("Telegram бот @{} успешно зарегистрирован", bot.getBotUsername());

            // Отправляем уведомление о запуске (опционально)
            sendStartupNotification(bot);

            return botsApi;
        } catch (TelegramApiException e) {
            log.error("Ошибка при регистрации Telegram бота", e);
            throw new RuntimeException("Не удалось зарегистрировать Telegram бота", e);
        }
    }

    private void sendStartupNotification(SeriesNotificationBot bot) {
        String adminChatId = System.getenv("TELEGRAM_ADMIN_CHAT_ID");
        if (adminChatId != null && !adminChatId.isEmpty()) {
            try {
                bot.sendTextMessage(Long.parseLong(adminChatId),
                        "Бот " + bot.getBotUsername() + " запущен!\n" +
                                "Время: " + java.time.LocalDateTime.now() + "\n" +
                                "Готов к работе!");
                log.info("Уведомление о запуске отправлено администратору");
            } catch (Exception e) {
                log.warn("Не удалось отправить уведомление о запуске", e);
            }
        }
    }
}
