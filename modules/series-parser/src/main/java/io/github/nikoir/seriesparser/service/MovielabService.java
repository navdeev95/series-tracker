package io.github.nikoir.seriesparser.service;

import io.github.nikoir.seriesparser.response.movielab.LoginResponse;
import io.github.nikoir.seriesparser.response.movielab.shorts.ContentItem;
import io.github.nikoir.seriesparser.response.movielab.shorts.ShortResponse;
import io.github.nikoir.seriesparser.response.movielab.stream.StreamResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@NoArgsConstructor
public class MovielabService {

    @Value("${api.movielab.short-info.url}")
    private String shortInfoUrl;

    @Value("${api.movielab.short-info.token}")
    private String shortInfoToken;

    @Value("${api.movielab.stream-info.url}")
    private String streamInfoUrl;

    @Value("${api.movielab.stream-info.client-id}")
    private String streamInfoClientId;

    @Value("${api.movielab.login.url}")
    private String loginUrl;

    @Value("${api.movielab.username}")
    private String userName;

    @Value("${api.movielab.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    // Инициализация headers в конструкторе или методе
    private MultiValueMap<String, String> getBaseHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("accept", "*/*");
        headers.add("accept-language", "ru");
        headers.add("content-type", "application/json");
        headers.add("origin", "https://movielab.one");
        headers.add("priority", "u=1, i");
        headers.add("referer", "https://movielab.one/");
        headers.add("sec-ch-ua", "\"Microsoft Edge\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"");
        headers.add("sec-ch-ua-mobile", "?0");
        headers.add("sec-ch-ua-platform", "\"Windows\"");
        headers.add("sec-fetch-dest", "empty");
        headers.add("sec-fetch-mode", "cors");
        headers.add("sec-fetch-site", "cross-site");
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0");
        return headers;
    }

    public StreamResponse getByKinopoiskId(Integer kinopoiskId) {
        // 1. Получаем короткую информацию
        ShortResponse shortResponse = getShortInfo(kinopoiskId);

        // 2. Извлекаем contentItem
        ContentItem contentItem = Optional.ofNullable(shortResponse)
                .map(ShortResponse::getData)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElseThrow(() -> new RuntimeException("No content found for kinopoiskId: " + kinopoiskId));

        // 3. Получаем токен авторизации
        String accessToken = getAccessToken();

        // 4. Получаем stream информацию
        return getStreamInfo(contentItem, accessToken);
    }

    private ShortResponse getShortInfo(Integer kinopoiskId) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(getBaseHeaders());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String shortUrl = String.format("%s?api_token=%s&kinopoisk_id=%s",
                shortInfoUrl,
                shortInfoToken,
                kinopoiskId);

        //ResponseEntity<String> testResponse = restTemplate.exchange(shortUrl, HttpMethod.GET, entity, String.class);

        ResponseEntity<ShortResponse> response = restTemplate.exchange(
                shortUrl, HttpMethod.GET, entity, ShortResponse.class);

        return response.getBody();
    }

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(getBaseHeaders());

        String requestBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", userName, password);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                loginUrl,
                HttpMethod.POST,
                entity,
                LoginResponse.class);

        return Optional.ofNullable(response.getBody())
                .map(LoginResponse::getAccessToken)
                .orElseThrow(() -> new RuntimeException("Failed to get access token"));
    }

    private StreamResponse getStreamInfo(ContentItem contentItem, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(getBaseHeaders());
        headers.add("Authorization", "Bearer " + accessToken); // Правильное добавление Authorization

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String streamUrl = String.format("%s?contentId=%s&contentType=%s&clientId=%s&domain=movielabone", // ⚠️ исправлено domain
                streamInfoUrl,
                contentItem.getId(),
                contentItem.getContentType().getApiValue(),
                streamInfoClientId);

        ResponseEntity<StreamResponse> response = restTemplate.exchange(
                streamUrl, HttpMethod.GET, entity, StreamResponse.class);

        return response.getBody();
    }
}