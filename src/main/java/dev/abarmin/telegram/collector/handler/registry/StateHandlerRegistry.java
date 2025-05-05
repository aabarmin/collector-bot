package dev.abarmin.telegram.collector.handler.registry;

import dev.abarmin.telegram.collector.handler.command.StateHandler;
import dev.abarmin.telegram.collector.service.ChatState;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class StateHandlerRegistry {

    private final Map<ChatState, StateHandler> handlers = new HashMap<>();

    public StateHandlerRegistry(Collection<StateHandler> commands) {
        for (StateHandler command : commands) {
            for (ChatState state : command.states()) {
                handlers.put(state, command);
            }
        }
    }

    public Optional<StateHandler> getHandler(ChatState state) {
        return Optional.ofNullable(handlers.get(state));
    }

}
