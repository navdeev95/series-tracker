package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.content.config.api.props.MovieLabProps;
import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
import io.github.nikoir.series.tracker.content.dto.integration.MovieLabEpisodeSearchRs;
import io.github.nikoir.series.tracker.content.adapter.season.MovieLabSeasonAdapter;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.EpisodeSearchStrategy;
import io.github.nikoir.series.tracker.content.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieLabEpisodeSearchStrategy implements EpisodeSearchStrategy {
    private final MovieLabSeasonAdapter seasonAdapter;
    private final RestTemplate restTemplate;
    private final HttpHeaders movieLabHeaders;
    private final MovieLabProps movieLabProps;

    @Override
    public Source getDataSource() {
        return Source.MOVIELAB;
    }

    @Override
    public List<SeasonViewRs> searchEpisodes(Map<ExternalId, String> externalIds) {
        String kinopoiskId = externalIds.get(ExternalId.KINOPOISK);
        if (StringUtils.isEmpty(kinopoiskId)) {
            throw new IllegalArgumentException("Not found kinopoisk id!"); //TODO: кастомные исключения
        }

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
