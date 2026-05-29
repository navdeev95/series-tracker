package io.github.nikoir.series.tracker.telegram.command.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CallbackCommandEnum implements CommandEnum {
    SERIES_DETAIL("series_detail", "Просмотр детальной информации о сериале"),
    SUBSCRIBE("subscribe", "Подписаться на сериал"),
    UNSUBSCRIBE("unsubscribe", "Отписаться от сериала");

    private final String text;
    private final String description;
}
