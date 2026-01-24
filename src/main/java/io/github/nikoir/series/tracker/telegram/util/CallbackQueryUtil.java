package io.github.nikoir.series.tracker.telegram.util;

import io.github.nikoir.series.tracker.telegram.model.CallbackQueryEnum;

import java.util.Optional;

public class CallbackQueryUtil {
    public static Optional<CallbackQueryEnum> extractCallbackQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Optional.empty();
        }

        String prefix = query.split(":")[0];
        if (prefix == null || prefix.trim().isEmpty()) {
            return Optional.empty();
        }
        return CallbackQueryEnum.fromPrefix(prefix);
    }

    public static String extractParameter(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "";
        }

        String param = query.split(":")[1];
        if (param == null || param.trim().isEmpty()) {
            return "";
        }
        return param;
    }
}
