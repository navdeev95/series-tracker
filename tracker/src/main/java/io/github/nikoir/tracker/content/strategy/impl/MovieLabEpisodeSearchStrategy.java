package io.github.nikoir.tracker.content.strategy.impl;

import io.github.nikoir.tracker.content.config.api.props.MovieLabProps;
import io.github.nikoir.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.tracker.content.dto.integration.MovieLabEpisodeSearchRs;
import io.github.nikoir.tracker.content.adapter.season.MovieLabSeasonAdapter;
import io.github.nikoir.common.dto.response.ExternalId;
import io.github.nikoir.tracker.content.enums.Source;
import io.github.nikoir.tracker.content.strategy.EpisodeSearchStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    public List<SeasonInfo> searchEpisodes(Map<ExternalId, String> externalIds) {
        String kinopoiskId = externalIds.get(ExternalId.KINOPOISK);
        if (StringUtils.isEmpty(kinopoiskId)) {
            throw new IllegalArgumentException("Not found kinopoisk id!"); //TODO: кастомные исключения
        }

        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String url = UriComponentsBuilder.fromUriString(movieLabProps.getUrl())
                .path(movieLabProps.getEpisodeSearch().getPath())
                .pathSegment(kinopoiskId)
                .build()
                .toUriString();

        ResponseEntity<MovieLabEpisodeSearchRs> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, MovieLabEpisodeSearchRs.class);

        List<MovieLabEpisodeSearchRs.Season> seasonList =  Optional.of(response)
                .map(HttpEntity::getBody)
                .map(MovieLabEpisodeSearchRs::result)
                .map(MovieLabEpisodeSearchRs.Result::serialEpisodes)
                .map(e -> e.values().stream().toList())
                .orElseThrow();

        return seasonAdapter.toSeasonInfoList(seasonList);
    }
}
