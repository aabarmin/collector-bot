package dev.abarmin.telegram.collector;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BaseTestConfiguration {

    @Bean
    public TestTelegramUser testTelegramClient(CollectorBot bot) {
        final TestTelegramBotAdapter adapter = new TestTelegramBotAdapter(bot);
        return new TestTelegramUser(adapter);
    }

    @Bean
    public TestTelegramMessageCollector messageCollector() {
        return new TestTelegramMessageCollector();
    }

    @Bean
    public TelegramClient telegramClient(TestTelegramMessageCollector collector) {
        return new TestTelegramClient(collector);
    }

}
