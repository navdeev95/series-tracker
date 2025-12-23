package io.github.nikoir.series.tracker.strategy.impl;

import io.github.nikoir.series.tracker.config.props.MovieLabProps;
import io.github.nikoir.series.tracker.dto.internal.SeasonViewRs;
import io.github.nikoir.series.tracker.dto.api.response.movielab.episode.search.MovieLabEpisodeSearchRs;
import io.github.nikoir.series.tracker.adapter.season.MovieLabSeasonAdapter;
import io.github.nikoir.series.tracker.enums.Source;
import io.github.nikoir.series.tracker.strategy.ExternalContentSearchStrategy;
import io.github.nikoir.series.tracker.strategy.SearchStrategy;
import io.github.nikoir.series.tracker.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieLabExternalContentSearchStrategy implements ExternalContentSearchStrategy {
    private final MovieLabSeasonAdapter seasonAdapter;
    private final RestTemplate restTemplate;
    private final HttpHeaders movieLabHeaders;
    private final MovieLabProps movieLabProps;

    @Override
    public Source getDataSource() {
        return Source.MOVIELAB;
    }

    @Override
    public List<SeasonViewRs> search(String kinopoiskId) {
        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String url = UriBuilder.from(movieLabProps.getUrl())
                .path(movieLabProps.getEpisodeSearch().getPath())
                .path(kinopoiskId)
                .build();

        ResponseEntity<MovieLabEpisodeSearchRs> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, MovieLabEpisodeSearchRs.class);

        List<MovieLabEpisodeSearchRs.Season> seasonList =  Optional.of(response)
                .map(HttpEntity::getBody)
                .map(MovieLabEpisodeSearchRs::result)
                .map(MovieLabEpisodeSearchRs.Result::serialEpisodes)
                .map(e -> e.values().stream().toList())
                .orElseThrow();

        return seasonAdapter.toSeasonViewRsList(seasonList);
    }
}
