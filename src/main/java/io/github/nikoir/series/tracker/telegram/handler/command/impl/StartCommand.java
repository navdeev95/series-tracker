package io.github.nikoir.series.tracker.telegram.handler.command.impl;

import io.github.nikoir.series.tracker.service.UserService;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.model.CommandEnum;
import io.github.nikoir.series.tracker.telegram.handler.command.Command;
import io.github.nikoir.series.tracker.telegram.model.ButtonEnum;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
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
    private final TelegramService telegramService;
    private final UserService userService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.START;
    }

    @Override
    public void execute(TelegramMessage message) {
        log.info("User {} ({}) has run bot", message.userId(), message.userName());
        userService.createIfNotExists(message.userId());

        // Регистрируем/получаем пользователя
        //userService.getOrCreateTelegramUser(userId, userName);

        // Отправляем сообщение с клавиатурой
        SendMessage response = SendMessage
                .builder()
                .chatId(message.chatId().toString())
                .text(getWelcomeMessage(message.userName()))
                .replyMarkup(createMainKeyboard())
                .build();

        try {
            telegramService.execute(response);
        } catch (TelegramApiException e) {
            log.error("Error while sending welcome message", e);
        }
    }

    private String getWelcomeMessage(String userName) {
        return String.format("""
            🎬 Привет, %s! Добро пожаловать!
            
            Я буду уведомлять тебя о выходе новых серий любимых сериалов.
            
            📋 Что я умею:
            • Уведомлять о новых сериях
            • Помогать искать сериалы
            • Управлять подписками
            
            🚀 Начни с команды /search чтобы найти первый сериал!
            """, userName);
    }

    private ReplyKeyboardMarkup createMainKeyboard() {

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
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboard(rows)
                .build();
    }
}