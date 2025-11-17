package io.github.nikoir.seriesparser.service;

import io.github.nikoir.seriesparser.config.MovieLabProperties;
import io.github.nikoir.seriesparser.dto.request.movielab.MovieLabLoginRq;
import io.github.nikoir.seriesparser.dto.response.movielab.LoginRs;
import io.github.nikoir.seriesparser.dto.response.movielab.shorts.ContentItem;
import io.github.nikoir.seriesparser.dto.response.movielab.shorts.ShortRs;
import io.github.nikoir.seriesparser.dto.response.movielab.stream.StreamRs;
import io.github.nikoir.seriesparser.util.JsonUtil;
import io.github.nikoir.seriesparser.util.UriBuilder;
import liquibase.util.UrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieLabService {
    private final MovieLabProperties properties;

    private final HttpHeaders movieLabHeaders;

    private final RestTemplate restTemplate;

    public StreamRs getByKinopoiskId(Integer kinopoiskId) {
        // 1. Получаем короткую информацию
        ShortRs shortRs = getShortInfo(kinopoiskId);

        // 2. Извлекаем contentItem
        ContentItem contentItem = Optional.ofNullable(shortRs)
                .map(ShortRs::data)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElseThrow(() -> new RuntimeException("No content found for kinopoiskId: " + kinopoiskId));

        // 3. Получаем токен авторизации
        String accessToken = getAccessToken();

        // 4. Получаем stream информацию
        return getStreamInfo(contentItem, accessToken);
    }

    private ShortRs getShortInfo(Integer kinopoiskId) {
        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String shortUrl = UriBuilder.from(properties.getShortInfo().getUrl())
                .param("api_token", properties.getShortInfo().getToken())
                .param("kinopoisk_id", kinopoiskId)
                .build();

        ResponseEntity<ShortRs> response = restTemplate.exchange(
                shortUrl, HttpMethod.GET, entity, ShortRs.class);

        return response.getBody();
    }

    //TODO: сделать refresh_token
    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(movieLabHeaders);

        MovieLabLoginRq loginRq = new MovieLabLoginRq(properties.getUsername(), properties.getPassword());

        HttpEntity<String> entity = new HttpEntity<>(JsonUtil.toJson(loginRq), headers);

        ResponseEntity<LoginRs> response = restTemplate.exchange(
                properties.getLogin().getUrl(),
                HttpMethod.POST,
                entity,
                LoginRs.class);

        return Optional.ofNullable(response.getBody())
                .map(LoginRs::accessToken)
                .orElseThrow(() -> new RuntimeException("Failed to get access token"));
    }

    private StreamRs getStreamInfo(ContentItem contentItem, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(movieLabHeaders);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String streamUrl = UriBuilder.from(properties.getStreamInfo().getUrl())
                .param("contentId", contentItem.id())
                .param("contentType", contentItem.contentType().getApiValue())
                .param("domain", "movielabone")
                .build();

        ResponseEntity<StreamRs> response = restTemplate.exchange(
                streamUrl, HttpMethod.GET, entity, StreamRs.class);

        return response.getBody();
    }
}