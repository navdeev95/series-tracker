package io.github.nikoir.seriesparser;

import io.github.nikoir.seriesparser.service.DbSeriesSearchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SeriesparserApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeriesparserApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(DbSeriesSearchService seriesSearchService) {
		return args -> {
			var result = seriesSearchService.findSeriesByTitle("title");
			System.out.println(result);
		};
	}
}
