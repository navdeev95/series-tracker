package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.content.config.api.props.WikidataProps;
import io.github.nikoir.series.tracker.content.dto.integration.TMDBExternalIdsRs;
import io.github.nikoir.series.tracker.content.dto.integration.WikidataItemRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.service.RequestBuilder;
import io.github.nikoir.series.tracker.content.strategy.ExternalIdStrategy;
import io.github.nikoir.series.tracker.content.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RequiredArgsConstructor
@Service
public class WikidataExternalIdStrategy implements ExternalIdStrategy {
    private final WikidataProps wikidataProps;
    private final RestTemplate restTemplate;
    private final RequestBuilder requestBuilder;


    @Override
    public Source getDataSource() {
        return Source.WIKIDATA;
    }

    @Override
    public Map<ExternalId, String> enrichExternalIds(Map<ExternalId, String> externalIds) {
        Map<ExternalId, String> result = new HashMap<>(externalIds);
        HttpEntity<String> authEntity = requestBuilder.buildAuthEntity(wikidataProps.getCredentials(), "token");
        String wikidataId = externalIds.get(ExternalId.WIKIDATA);

        if (StringUtils.isEmpty(wikidataId)) {
            throw new IllegalArgumentException("Not found wikidata id!"); //TODO: кастомные исключения
        }

        String url = UriBuilder.from(wikidataProps.getUrl())
                .path(wikidataProps.getGetEntity().getPath())
                .var("item_id", wikidataId)
                .disableEncoding()
                .build();

        ResponseEntity<WikidataItemRs> response = restTemplate.exchange(url,
                HttpMethod.GET,
                authEntity,
                WikidataItemRs.class);

        if (response.hasBody()) {
            WikidataItemRs responseBody = response.getBody();
            Optional<String> kinopoiskId = extractKinopoiskId(responseBody);
            kinopoiskId.ifPresent(string -> putIfNotBlankAndNotContains(result, ExternalId.KINOPOISK, string));
        }

        return result;
    }

    private Optional<String> extractKinopoiskId(WikidataItemRs responseBody) {
        if (responseBody == null) {
            return Optional.empty();
        }

        Map<String, List<WikidataItemRs.WikidataStatement>> statements = responseBody.statements();
        if (statements == null) {
            return Optional.empty();
        }

        List<WikidataItemRs.WikidataStatement> kinopoiskStatements = statements.get("P2603");
        if (kinopoiskStatements == null || kinopoiskStatements.isEmpty()) {
            return Optional.empty();
        }

        WikidataItemRs.WikidataStatement firstStatement = kinopoiskStatements.getFirst();
        WikidataItemRs.WikidataStatementValue value = firstStatement.value();
        if (value == null) {
            return Optional.empty();
        }

        Object content = value.content();
        if (content == null) {
            return Optional.empty();
        }

        return Optional.of(content.toString());
    }
}
