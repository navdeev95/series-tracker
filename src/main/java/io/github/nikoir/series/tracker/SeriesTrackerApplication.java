package io.github.nikoir.series.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SeriesTrackerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeriesTrackerApplication.class, args);
	}
}
