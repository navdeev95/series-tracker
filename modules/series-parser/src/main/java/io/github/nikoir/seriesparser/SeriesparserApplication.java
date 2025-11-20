package io.github.nikoir.seriesparser;

import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.service.episode.search.MovieLabExternalContentSearchStrategy;
import io.github.nikoir.seriesparser.service.series.search.MovieLabSearchStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SeriesparserApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeriesparserApplication.class, args);
	}
}
