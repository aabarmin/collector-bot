package dev.abarmin.telegram.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CollectorBotConfiguration.class)
public class CollectorBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollectorBotApplication.class, args);
	}

}

