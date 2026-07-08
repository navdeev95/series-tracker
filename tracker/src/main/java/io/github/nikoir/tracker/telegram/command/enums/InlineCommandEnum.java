package io.github.nikoir.tracker.telegram.command.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InlineCommandEnum implements CommandEnum {
    SEARCH("search", "Поиск сериала"),
    SUBSCRIPTIONS("subscriptions", "Список подписок на сериал");

    private final String text;
    private final String description;

    @Override
    public String getSeparator() {
        return ":";
    }
}
