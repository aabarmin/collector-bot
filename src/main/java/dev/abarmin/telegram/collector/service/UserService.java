package dev.abarmin.telegram.collector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public UserEntity getUser(Update update) {
        final long chatId = UpdateHelper.getChatId(update);
        return userRepository.findByChatId(chatId)
                .orElseGet(() -> createUser(update));
    }

    public UserEntity getUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private UserEntity createUser(Update update) {
        final UserEntity user = UserEntity.builder()
                .chatId(update.getMessage().getChatId())
                .username(update.getMessage().getFrom().getUserName())
                .state(ChatState.STARTED)
                .build();

        return userRepository.save(user);
    }

    public ChatState getState(Update update) {
        final UserEntity user = getUser(update);
        if (user.getState() == null) {
            user.setState(ChatState.STARTED);
            userRepository.save(user);
        }
        return user.getState();
    }

    public ChatState setState(Update update, ChatState state) {
        final UserEntity user = getUser(update);
        user.setState(state);
        userRepository.save(user);
        return state;
    }

    @SneakyThrows
    public <T> T getContext(Update update, Class<T> clazz) {
        final UserEntity user = getUser(update);
        if (user.getContext() == null) {
            return null;
        }
        return objectMapper.readValue(user.getContext(), clazz);
    }

    @SneakyThrows
    public <T> T setContext(Update update, T context) {
        final UserEntity user = getUser(update);
        if (context == null) {
            user.setContext(null);
        } else {
            user.setContext(objectMapper.writeValueAsString(context));
        }
        userRepository.save(user);
        return context;
    }
}
