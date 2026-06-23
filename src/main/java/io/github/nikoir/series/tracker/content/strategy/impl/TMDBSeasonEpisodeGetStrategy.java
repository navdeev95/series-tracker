package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.content.adapter.episode.TMDBEpisodeAdapter;
import io.github.nikoir.series.tracker.content.adapter.season.TMDBSeasonAdapter;
import io.github.nikoir.series.tracker.content.config.api.props.TMDBProps;
import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeasonInfoRs;
import io.github.nikoir.series.tracker.content.dto.internal.EpisodeInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBSeriesInfoRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.service.RequestBuilder;
import io.github.nikoir.series.tracker.content.service.TMDBCachedService;
import io.github.nikoir.series.tracker.content.strategy.SeasonEpisodeGetStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TMDBSeasonEpisodeGetStrategy implements SeasonEpisodeGetStrategy {
    private final TMDBCachedService tmdbCachedService;
    private final TMDBSeasonAdapter seasonAdapter;
    private final TMDBEpisodeAdapter episodeAdapter;

    private final TMDBProps tmdbProps;
    private final RequestBuilder requestBuilder;
    private final RestTemplate restTemplate;

    @Override
    public List<SeasonInfo> getSeasonsWithEpisodes(Map<ExternalId, String> externalIds) {
        String tmdbId = externalIds.get(ExternalId.TMDB);
        if (StringUtils.isEmpty(tmdbId)) {
            throw new IllegalArgumentException("Not found tmdb id!"); //TODO: кастомные исключения
        }
        Optional<TMDBSeriesInfoRs> response = tmdbCachedService.getSeriesInfo(tmdbId);
        if (response.isEmpty()) {
            return Collections.emptyList();
        }

        List<SeasonInfo> seasonInfoList = seasonAdapter.toSeasonInfoList(response.get().seasons());
        for (SeasonInfo seasonInfo: seasonInfoList) {
            List<EpisodeInfo> foundEpisodes = getEpisodesList(tmdbId, seasonInfo.getNumber());
            seasonInfo.setEpisodes(foundEpisodes);
        }
        return seasonInfoList;
    }


    private List<EpisodeInfo> getEpisodesList(String tmdbId, Integer seasonNumber) {
        HttpEntity<String> authEntity = requestBuilder.buildAuthEntity(tmdbProps.getCredentials(), "token");
        String url = UriComponentsBuilder.fromUriString(tmdbProps.getUrl())
                .path(tmdbProps.getSeasonDetails().getPath())
                .queryParam("language", "ru-RU")
                .build(false)
                .expand(tmdbId, seasonNumber)
                .toUriString();

        ResponseEntity<TMDBSeasonInfoRs> response = restTemplate.exchange(url,
                HttpMethod.GET,
                authEntity,
                TMDBSeasonInfoRs.class);
        if (!response.hasBody() || !response.getStatusCode().is2xxSuccessful()) {
            return Collections.emptyList();
        }

        return episodeAdapter.toEpisodeInfoList(response.getBody().episodes());
    }
}
