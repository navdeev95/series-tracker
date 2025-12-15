package io.github.nikoir.series.tracker.telegram.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandRegistry {
    private final List<Command> commands;
    private final Map<String, Command> commandMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Command command : commands) {
            commandMap.put(command.getCommand().getCommandText(), command);
            log.debug("Зарегистрирована команда: {}", command.getCommand());
        }
        log.info("Зарегистрировано команд: {}", commands.size());
    }

    public Command getCommand(String commandName) {
        return commandMap.get(commandName);
    }

    public boolean hasCommand(String commandName) {
        return commandMap.containsKey(commandName);
    }

    public List<Command> getAllCommands() {
        return commands;
    }
}