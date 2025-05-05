package dev.abarmin.telegram.collector.handler.registry;

import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandlerRegistry {

    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public CommandHandlerRegistry(Collection<CommandHandler> commands) {
        for (CommandHandler command : commands) {
            for (String cmd : command.commands()) {
                handlers.put(cmd, command);
            }
        }
    }

    public Optional<CommandHandler> getHandler(String command) {
        return Optional.ofNullable(handlers.get(command));
    }
}
