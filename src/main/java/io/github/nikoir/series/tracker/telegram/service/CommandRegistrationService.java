package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.model.BotCommandEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandRegistrationService {
    private final SeriesNotificationBot bot;

    @PostConstruct
    public void registerCommands() {
        try {
            List<BotCommand> commands = Arrays.stream(BotCommandEnum.values())
                    .map(cmd -> new BotCommand(cmd.getCommandText(), cmd.getDescription()))
                    .collect(Collectors.toList());

            SetMyCommands setCommands = new SetMyCommands();
            setCommands.setCommands(commands);
            setCommands.setScope(new BotCommandScopeDefault());
            setCommands.setLanguageCode("ru");

            bot.execute(setCommands);
            log.info("✅ Команды меню зарегистрированы в Telegram: {} команд", commands.size());

        } catch (TelegramApiException e) {
            log.error("❌ Ошибка при регистрации команд меню", e);
        }
    }
}
