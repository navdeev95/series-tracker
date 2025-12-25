package io.github.nikoir.series.tracker.telegram.handler.impl;

import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.model.BotCommandEnum;
import io.github.nikoir.series.tracker.telegram.handler.Command;
import io.github.nikoir.series.tracker.telegram.model.ButtonEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    //private final UserService userService;
    private final SeriesNotificationBot bot;

    @Override
    public BotCommandEnum getCommand() {
        return BotCommandEnum.START;
    }

    @Override
    public void execute(TelegramMessage message) {
        log.info("User {} ({}) has run bot", message.userId(), message.userName());

        // Регистрируем/получаем пользователя
        //userService.getOrCreateTelegramUser(userId, userName);

        // Отправляем сообщение с клавиатурой
        SendMessage response = new SendMessage();
        response.setChatId(message.chatId().toString());
        response.setText(getWelcomeMessage(message.userName()));
        response.setReplyMarkup(createMainKeyboard());

        try {
            bot.execute(response);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке приветствия", e);
        }
    }

    private String getWelcomeMessage(String userName) {
        return String.format("""
            🎬 Привет, %s! Добро пожаловать в %s!
            
            Я буду уведомлять тебя о выходе новых серий любимых сериалов.
            
            📋 Что я умею:
            • Уведомлять о новых сериях
            • Помогать искать сериалы
            • Управлять подписками
            
            🚀 Начни с команды /search чтобы найти первый сериал!
            """, userName, bot.getBotName());
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);

        // Получаем кнопки для главного меню
        List<ButtonEnum> mainButtons = ButtonEnum.getMainMenuButtons();

        // Группируем по 2 кнопки
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < mainButtons.size(); i += 2) {
            KeyboardRow row = new KeyboardRow();

            row.add(mainButtons.get(i).getDisplayText());

            if (i + 1 < mainButtons.size()) {
                row.add(mainButtons.get(i + 1).getDisplayText());
            }

            rows.add(row);
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }
}