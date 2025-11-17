package io.github.nikoir.seriesparser;

import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.service.MovieLabService;
import io.github.nikoir.seriesparser.service.episode.search.MovieLabEpisodeSearchStrategy;
import io.github.nikoir.seriesparser.service.series.search.MovieLabSearchStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static io.github.nikoir.seriesparser.enums.ExternalId.KINOPOISK;

@SpringBootApplication
public class SeriesparserApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeriesparserApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(MovieLabSearchStrategy seriesSearchService, MovieLabEpisodeSearchStrategy movieLabEpisodeSearchStrategy) {
		return args -> {
			var result = seriesSearchService.search(new SeriesSearchRq("как приручить лису", 1, 20));
			var series = result.getContent().stream().filter(SeriesViewRs::isSeries).toList();

			var firstResult = series.getFirst();

			var seasons = movieLabEpisodeSearchStrategy.search(firstResult.externalIds());

			System.out.println(seasons);

		};
	}
}
