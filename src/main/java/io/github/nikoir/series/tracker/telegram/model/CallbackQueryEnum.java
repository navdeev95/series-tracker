package io.github.nikoir.series.tracker.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum CallbackQueryEnum {
    SERIES_DETAIL("series_detail");
    private final String prefix;

    public static Optional<CallbackQueryEnum> fromPrefix(String prefix) {
        return Arrays
                .stream(values())
                .filter(val -> prefix.equals(val.prefix))
                .findFirst();
    }
}
