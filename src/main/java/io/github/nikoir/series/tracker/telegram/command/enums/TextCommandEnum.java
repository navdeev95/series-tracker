package io.github.nikoir.series.tracker.telegram.command.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TextCommandEnum implements CommandEnum {
    START("start", "Запустить бота"),
    HELP("help", "Помощь и команды"),
    SEARCH("search", "Найти сериал"),
    MY_SUBSCRIPTIONS("mysubscriptions", "Мои подписки"),
    SETTINGS("settings", "Настройки уведомлений");

    private final String text;
    private final String description;

    @Override
    public String getPrefix() {
        return "/";
    }
}
