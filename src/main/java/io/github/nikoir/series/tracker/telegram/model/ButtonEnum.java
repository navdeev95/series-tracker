package io.github.nikoir.series.tracker.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ButtonEnum {
    SEARCH("🔍 Поиск сериалов", CommandEnum.SEARCH, true),
    MY_SUBSCRIPTIONS("📋 Мои подписки", CommandEnum.MY_SUBSCRIPTIONS, true),
    SETTINGS("⚙️ Настройки", CommandEnum.SETTINGS, true),
    HELP("❓ Помощь", CommandEnum.HELP, true);

    private final String displayText;
    private final CommandEnum command;
    private final boolean isInMainMenu;

    public static List<ButtonEnum> getMainMenuButtons() {
        return Arrays.stream(values())
                .filter(ButtonEnum::isInMainMenu)
                .collect(Collectors.toList());
    }

    public static Optional<ButtonEnum> fromDisplayText(String displayText) {
        return Arrays.stream(values())
                .filter(btn -> btn.getDisplayText().equals(displayText))
                .findFirst();
    }

    public static Optional<ButtonEnum> fromCommandText(String commandText) {
        return Arrays.stream(values())
                .filter(btn -> btn
                        .getCommand()
                        .getCommandText().equals(commandText))
                .findFirst();
    }
}
