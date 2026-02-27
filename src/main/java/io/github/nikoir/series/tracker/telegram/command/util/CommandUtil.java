package io.github.nikoir.series.tracker.telegram.command.util;

import io.github.nikoir.series.tracker.telegram.command.enums.CommandEnum;
import io.github.nikoir.series.tracker.telegram.command.enums.TextCommandEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandUtil {
    private static final String BOT_NAME_SEPARATOR = "@";

    public static boolean isTextCommand(String commandText) {
        //TODO: подумать над тем, как сделать лучше
        return commandText.startsWith(TextCommandEnum.values()[0].getPrefix());
    }

    public static <T extends Enum<T> & CommandEnum> String extractCommandText(Class<T> commandEnum, String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }

        String firstPart = text.split(String.valueOf(commandEnum.getEnumConstants()[0].getSeparator()))[0];

        return firstPart.split(BOT_NAME_SEPARATOR)[0].trim().toLowerCase();
    }

    public static <T extends Enum<T> & CommandEnum> List<String> extractParameters(T commandEnum, String text) {
        if (StringUtils.isBlank(text)) {
            return List.of();
        }

        String[] parts = text.split(String.valueOf(commandEnum.getSeparator()));
        if (parts.length < 2) {
            return List.of();
        }

        // Пропускаем первый элемент (саму команду)
        return Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
    }

    public static <T extends Enum<T> & CommandEnum> Optional<String> extractFirstParameter(T commandEnum, String text) {
        List<String> params = extractParameters(commandEnum, text);
        return params.isEmpty() ? Optional.empty() : Optional.of(params.get(0));
    }

    public static <T extends Enum<T> & CommandEnum> String createCommandString(T commandEnum, String... args) {
        StringBuilder result = new StringBuilder(commandEnum.getText());
        result.append(commandEnum.getSeparator());
        if (args.length > 0) {
            result.append(String.join(String.valueOf(commandEnum.getSeparator()), args));
        }

        return result.toString();
    }

    public static <T extends Enum<T> & CommandEnum> Optional<T> fromCommandText(Class<T> enumClass,
                                                                                String commandText) {
        if (StringUtils.isEmpty(commandText)) {
            return Optional.empty();
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(cmd -> cmd.isCommandTextEquals(commandText))
                .findFirst();
    }
}
