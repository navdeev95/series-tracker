package io.github.nikoir.series.tracker.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//TODO: переместить в другое место
@Component
@RequiredArgsConstructor
public class RequestBuilder {

    public HttpEntity<String> buildAuthEntity(Map<String, String> credentials, String key) {//TODO: создать enum
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(credentials.get(key));
        return new HttpEntity<>(headers);
    }
}