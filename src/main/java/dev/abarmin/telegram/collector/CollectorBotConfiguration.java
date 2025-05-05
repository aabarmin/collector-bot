package dev.abarmin.telegram.collector;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties
public class CollectorBotConfiguration {

    @Valid
    @NotNull
    private Bots bots = new Bots();

    @Data
    public static class Bots {

        @Valid
        @NotNull
        private Collector collector = new Collector();

    }

    @Data
    public static class Collector {

        @NotEmpty
        private String token;

        @NotEmpty
        private String name;

    }
}
