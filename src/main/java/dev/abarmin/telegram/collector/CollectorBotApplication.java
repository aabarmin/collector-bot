package dev.abarmin.telegram.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties(CollectorBotConfiguration.class)
public class CollectorBotApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CollectorBotApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(CollectorBotApplication.class);
	}
}
