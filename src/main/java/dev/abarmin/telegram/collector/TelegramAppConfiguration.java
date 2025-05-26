package dev.abarmin.telegram.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnMissingBean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(getCollectorBotToken());
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(
            name = "bots.collector.get-updates-strategy",
            havingValue = "LONG_POLLING",
            matchIfMissing = true
    )
    public TelegramBotsLongPollingApplication longPollingApplication(CollectorBot bot) throws TelegramApiException {
        final TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
        final LongPollingTelegramBotAdapter adapter = new LongPollingTelegramBotAdapter(bot);
        app.registerBot(getCollectorBotToken(), adapter);
        return app;
    }

}
