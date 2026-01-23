package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.telegram.model.CommandEnum;
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
    private final TelegramService telegramService;

    @PostConstruct
    public void registerCommands() {
        try {
            List<BotCommand> commands = Arrays.stream(CommandEnum.values())
                    .map(cmd -> new BotCommand(cmd.getCommandText(), cmd.getDescription()))
                    .collect(Collectors.toList());

            SetMyCommands setCommands = SetMyCommands
                    .builder()
                    .commands(commands)
                    .scope(new BotCommandScopeDefault())
                    .languageCode("ru")
                    .build();

            telegramService.execute(setCommands);
            log.info("✅ Commands registered in Telegram: {} commands", commands.size());

        } catch (TelegramApiException e) {
            log.error("❌ Error while registering telegram commands", e);
        }
    }
}
