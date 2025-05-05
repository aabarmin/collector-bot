package dev.abarmin.telegram.collector.handler.registry;

import dev.abarmin.telegram.collector.handler.command.CallbackHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CallbackHandlerRegistry {

    private final Map<String, CallbackHandler> handlers;

    public CallbackHandlerRegistry(Collection<CallbackHandler> commands) {
        handlers = commands.stream()
                .collect(Collectors.toMap(CallbackHandler::callback, Function.identity()));
    }

    public Optional<CallbackHandler> getHandler(String command) {
        return Optional.ofNullable(handlers.get(command));
    }
}
