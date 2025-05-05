package dev.abarmin.telegram.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramAppConfiguration {

    @Autowired
    private CollectorBotConfiguration botConfiguration;

    private String getCollectorBotToken() {
        return botConfiguration.getBots().getCollector().getToken();
    }

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(getCollectorBotToken());
    }

    @Bean
    public TelegramBotsLongPollingApplication longPollingApplication(CollectorBot bot) throws TelegramApiException {
        final TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
        app.registerBot(getCollectorBotToken(), bot);
        return app;
    }

}
