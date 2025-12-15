package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.config.props.LumexPortalProps;
import io.github.nikoir.series.tracker.config.props.LumexSiteProps;
import io.github.nikoir.series.tracker.dto.api.request.movielab.MovieLabLoginRq;
import io.github.nikoir.series.tracker.dto.api.response.lumex.login.LoginRs;
import io.github.nikoir.series.tracker.dto.api.response.lumex.shorts.ShortRs;
import io.github.nikoir.series.tracker.dto.api.response.lumex.stream.StreamRs;
import io.github.nikoir.series.tracker.util.JsonUtil;
import io.github.nikoir.series.tracker.util.UriBuilder;
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
public class LumexService {
    private final LumexPortalProps lumexPortalProps;
    private final LumexSiteProps lumexSiteProps;
    private final HttpHeaders movieLabHeaders;
    private final RestTemplate restTemplate;

    public StreamRs getByKinopoiskId(Integer kinopoiskId) {
        // 1. Получаем короткую информацию
        ShortRs shortRs = getShortInfo(kinopoiskId);

        // 2. Извлекаем contentItem
        ShortRs.ContentItem contentItem = Optional.ofNullable(shortRs)
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
        String token = lumexPortalProps.getCredentials().get("token");

        HttpEntity<String> entity = new HttpEntity<>(movieLabHeaders);

        String shortUrl = UriBuilder.from(lumexPortalProps.getUrl())
                .path(lumexPortalProps.getShortInfo().getPath())
                .param("api_token", token)
                .param("kinopoisk_id", kinopoiskId)
                .build();

        ResponseEntity<ShortRs> response = restTemplate.exchange(
                shortUrl, HttpMethod.GET, entity, ShortRs.class);

        return response.getBody();
    }

    //TODO: сделать refresh_token
    private String getAccessToken() {
        String login = lumexSiteProps.getCredentials().get("username");
        String password = lumexSiteProps.getCredentials().get("password");

        HttpHeaders headers = new HttpHeaders();
        headers.addAll(movieLabHeaders);

        MovieLabLoginRq loginRq = new MovieLabLoginRq(login, password);

        HttpEntity<String> entity = new HttpEntity<>(JsonUtil.toJson(loginRq), headers);

        String url = UriBuilder.from(lumexSiteProps.getUrl())
                .path(lumexSiteProps.getLogin().getPath())
                .build();

        ResponseEntity<LoginRs> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                LoginRs.class);

        return Optional.ofNullable(response.getBody())
                .map(LoginRs::accessToken)
                .orElseThrow(() -> new RuntimeException("Failed to get access token"));
    }

    private StreamRs getStreamInfo(ShortRs.ContentItem contentItem, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(movieLabHeaders);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String streamUrl = UriBuilder.from(lumexSiteProps.getUrl())
                .path(lumexSiteProps.getStreamInfo().getPath())
                .param("contentId", contentItem.id())
                .param("contentType", contentItem.contentType().getApiValue())
                .param("domain", "movielabone")
                .build();

        ResponseEntity<StreamRs> response = restTemplate.exchange(
                streamUrl, HttpMethod.GET, entity, StreamRs.class);

        return response.getBody();
    }
}