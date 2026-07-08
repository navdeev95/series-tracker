package io.github.nikoir.tracker.telegram.command.enums;

public interface CommandEnum {
    String getText();
    String getDescription();
    default String getSeparator() {
        return " ";
    }
    default String getPrefix() {
        return "";
    }

    default boolean isCommandTextEquals(String commandText) {
        return (getPrefix() + getText()).equals(commandText);
    }
}
