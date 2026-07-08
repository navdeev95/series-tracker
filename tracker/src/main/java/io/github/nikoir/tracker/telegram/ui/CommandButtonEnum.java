package io.github.nikoir.tracker.telegram.ui;

import io.github.nikoir.tracker.telegram.command.enums.TextCommandEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum CommandButtonEnum {
    SEARCH("🔍 Поиск сериалов", TextCommandEnum.SEARCH, true),
    MY_SUBSCRIPTIONS("📋 Мои подписки", TextCommandEnum.MY_SUBSCRIPTIONS, true),
    SETTINGS("⚙️ Настройки", TextCommandEnum.SETTINGS, true),
    HELP("❓ Помощь", TextCommandEnum.HELP, true);

    private final String displayText;
    private final TextCommandEnum command;
    private final boolean isInMainMenu;

    public static List<CommandButtonEnum> getMainMenuButtons() {
        return Arrays.stream(values())
                .filter(CommandButtonEnum::isInMainMenu)
                .collect(Collectors.toList());
    }

    public static Optional<CommandButtonEnum> fromDisplayText(String displayText) {
        return Arrays.stream(values())
                .filter(btn -> btn.getDisplayText().equals(displayText))
                .findFirst();
    }
}
