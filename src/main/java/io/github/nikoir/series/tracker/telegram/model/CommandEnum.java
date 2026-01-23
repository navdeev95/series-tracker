package io.github.nikoir.series.tracker.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandEnum {
    START("/start", "Запустить бота"),
    HELP("/help", "Помощь и команды"),
    SEARCH("/search", "Найти сериал"),
    MY_SUBSCRIPTIONS("/mysubscriptions", "Мои подписки"),
    SETTINGS("/settings", "Настройки уведомлений");

    private final String commandText;
    private final String description;
}
