package io.github.nikoir.seriesparser.service.episode.search;

import io.github.nikoir.seriesparser.config.props.MovieLabProps;
import io.github.nikoir.seriesparser.dto.response.SeasonViewRs;
import io.github.nikoir.seriesparser.dto.response.movielab.episode.search.MovieLabEpisodeSearchRs;
import io.github.nikoir.seriesparser.mapper.MovieLabEpisodeMapper;
import io.github.nikoir.seriesparser.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.nikoir.seriesparser.enums.ExternalId.KINOPOISK;

@Service
@RequiredArgsConstructor
public class MovieLabEpisodeSearchStrategy implements EpisodeSearchStrategy {
    private final MovieLabEpisodeMapper episodeMapper;
    private final RestTemplate restTemplate;
    private final HttpHeaders movieLabHeaders;
    private final MovieLabProps movieLabProps;

    @Override
    public List<SeasonViewRs> search(Map<String, String> externalIds) {
        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String kinopoiskId = externalIds.get(KINOPOISK.getSourceName());

        String url = UriBuilder.from(movieLabProps.getUrl())
                .path(movieLabProps.getEpisodeSearch().getPath())
                .path(kinopoiskId)
                .build();

        ResponseEntity<MovieLabEpisodeSearchRs> response = restTemplate.getForEntity(url, MovieLabEpisodeSearchRs.class);

        List<MovieLabEpisodeSearchRs.Season> seasonList =  Optional.of(response)
                .map(HttpEntity::getBody)
                .map(MovieLabEpisodeSearchRs::result)
                .map(MovieLabEpisodeSearchRs.Result::serialEpisodes)
                .map(e -> e.values().stream().toList())
                .orElseThrow();

        return episodeMapper.toSeasonViewRsList(seasonList);
    }
}
