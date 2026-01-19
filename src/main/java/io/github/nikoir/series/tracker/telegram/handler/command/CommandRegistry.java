package io.github.nikoir.series.tracker.telegram.handler.command;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            log.debug("Command registered: {}", command.getCommand());
        }
        log.info("Commands registered: {}", commands.size());
    }

    public Optional<Command> getCommand(String commandText) {
        return Optional.ofNullable(commandMap.get(commandText));
    }

    public boolean hasCommand(String commandName) {
        return commandMap.containsKey(commandName);
    }

    public List<Command> getAllCommands() {
        return commands;
    }
}