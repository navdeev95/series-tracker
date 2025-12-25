package io.github.nikoir.series.tracker.telegram.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandUtil {
    public static String extractCommand(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] parts = text.split("\\s+")[0].split("@");
        return parts[0].toLowerCase().trim();
    }

    public static List<String> extractParameters(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        String[] parts = text.split("\\s+");
        if (parts.length < 2) {
            return List.of();
        }

        // Пропускаем первый элемент (саму команду)
        return Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
    }

    public static Optional<String> extractParameterString(String text) {
        List<String> params = extractParameters(text);
        if (params.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(String.join(" ", params));
    }

    public static Optional<String> extractFirstParameter(String text) {
        List<String> params = extractParameters(text);
        return params.isEmpty() ? Optional.empty() : Optional.of(params.get(0));
    }

    public static Optional<String> extractNthParameter(String text, int index) {
        List<String> params = extractParameters(text);
        if (index < 0 || index >= params.size()) {
            return Optional.empty();
        }
        return Optional.of(params.get(index));
    }

    public static boolean isCommand(String text) {
        return text != null && text.startsWith("/");
    }

    public static String normalizeCommand(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] parts = text.split("\\s+", 2);
        String commandPart = parts[0];

        // Удаляем @botname если есть
        String normalizedCommand = commandPart.split("@")[0];

        // Восстанавливаем параметры если они были
        if (parts.length > 1) {
            return normalizedCommand + " " + parts[1];
        }

        return normalizedCommand;
    }
}
