package io.github.nikoir.series.tracker.telegram.ui.factory;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.series.tracker.telegram.ui.CommandButtonEnum;
import io.github.nikoir.series.tracker.telegram.model.SubscriptionStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum.*;
import static io.github.nikoir.series.tracker.telegram.command.util.CommandUtil.createCommandString;

@Component
public class KeyboardFactory {
    public InlineKeyboardMarkup createSeriesKeyboard(SeriesDetailPersonalizedRs seriesDetail, String seriesToken) {
        SubscriptionStatus status = seriesDetail.isUserSubscribed() ?
                SubscriptionStatus.SUBSCRIBED : SubscriptionStatus.NOT_SUBSCRIBED;
        return createSubscriptionKeyboard(seriesToken, status);
    }

    public InlineKeyboardMarkup createSubscriptionKeyboard(String seriesToken, SubscriptionStatus state) {
        InlineKeyboardRow row = switch (state) {
            case SUBSCRIBED -> new InlineKeyboardRow(createUnsubscribeButton(seriesToken));
            case NOT_SUBSCRIBED -> new InlineKeyboardRow(createSubscribeButton(seriesToken));
            case WAITING -> new InlineKeyboardRow(createWaitingButton());
        };

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(row))
                .build();
    }

    public InlineKeyboardMarkup createPreviewKeyboard(String token) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(createDetailButton(token)))
                .build();
    }

    public InlineKeyboardMarkup createSearchKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singleton(new InlineKeyboardRow(createSearchButton())))
                .build();
    }

    public ReplyKeyboardMarkup createMainKeyboard() {
        // Получаем кнопки для главного меню
        List<CommandButtonEnum> mainButtons = CommandButtonEnum.getMainMenuButtons();

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

    private InlineKeyboardButton createSubscribeButton(String seriesToken) {
        return InlineKeyboardButton.builder()
                .text("🔔 Подписаться")
                .callbackData(createCommandString(SUBSCRIBE, seriesToken))
                .build();
    }

    private InlineKeyboardButton createUnsubscribeButton(String seriesToken) {
        return InlineKeyboardButton.builder()
                .text("🔕 Отписаться")
                .callbackData(createCommandString(UNSUBSCRIBE, seriesToken))
                .build();
    }

    private InlineKeyboardButton createWaitingButton() {
        return InlineKeyboardButton.builder()
                .text("⏳ Обработка...")
                .callbackData(" ")
                .build();
    }

    private InlineKeyboardButton createDetailButton(String token) {
        return InlineKeyboardButton.builder()
                .text("ℹ️ Подробнее")
                .callbackData(createCommandString(SERIES_DETAIL, token))
                .build();
    }

    private InlineKeyboardButton createSearchButton() {
        return InlineKeyboardButton.builder()
                .text("🔍 Поиск")
                .switchInlineQueryCurrentChat(createCommandString(InlineCommandEnum.SEARCH))
                .build();
    }
}