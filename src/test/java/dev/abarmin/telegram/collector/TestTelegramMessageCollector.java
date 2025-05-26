package dev.abarmin.telegram.collector;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TestTelegramMessageCollector {

    private Map<String, List<MessageWrapper>> messages = new HashMap<>();

    public void collect(SendMessage message) {
        final String chatId = message.getChatId();
        final List<MessageWrapper> chatMessages = messages.computeIfAbsent(chatId, k -> new ArrayList<>());
        chatMessages.add(new MessageWrapper(message));
    }

    public void messagesAfter(long chatId, Instant timestamp, Consumer<List<SendMessage>> consumer) {
        final List<SendMessage> chatMessages = messages.getOrDefault(String.valueOf(chatId), List.of()).stream()
                .filter(wrapper -> wrapper.createdAt.isAfter(timestamp))
                .map(MessageWrapper::message)
                .toList();

        consumer.accept(chatMessages);
    }

    record MessageWrapper(SendMessage message, Instant createdAt) {
        MessageWrapper(SendMessage message) {
            this(message, Instant.now());
        }
    }
}
