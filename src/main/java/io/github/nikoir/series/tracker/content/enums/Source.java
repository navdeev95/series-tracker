package io.github.nikoir.series.tracker.content.enums;

import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum Source {
    DATABASE("DataBase", false, null),
    KINOPOISK("KinoPoisk", true, ExternalId.KINOPOISK),
    MOVIELAB("MovieLab", true, ExternalId.KINOPOISK),
    TMDB("TMDB", true, ExternalId.TMDB),
    WIKIDATA("WikiData", true, ExternalId.WIKIDATA);

    private final String name;
    private final boolean external;
    private final ExternalId requiredExternalId;

    private Long entityId;
    private String rootUrl;
    private String urlTemplate;

    public synchronized void initFromEntity(DictSource entity) {
        if (entity != null && this.name.equalsIgnoreCase(entity.getName())) {
            this.entityId = entity.getId();
            this.rootUrl = entity.getRootUrl();
            this.urlTemplate = entity.getUrlTemplate();
        }
    }

    public static Source fromName(String name) {
        return Arrays.stream(values())
                .filter(source -> source.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown data source: " + name));
    }

    public static Source fromEntity(DictSource entity) {
        Source source = fromName(entity.getName());
        source.initFromEntity(entity);
        return source;
    }

    public static Optional<String> buildUrl(DictSource dictSource, Map<ExternalId, String> externalIds) {
        Source source = Source.fromName(dictSource.getName());

        return Optional.ofNullable(externalIds.get(source.getRequiredExternalId()))
                .filter(StringUtils::isNotEmpty)
                .map(id -> UriComponentsBuilder
                        .fromUriString(dictSource.getUrlTemplate())
                        .build()
                        .expand(id)
                        .toUriString());
    }

}
