package io.github.nikoir.series.tracker.content.enums;

import io.github.nikoir.series.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictExternalId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Getter
@RequiredArgsConstructor
public enum ExternalId {
    KINOPOISK("kinopoisk"),
    IMDB("IMDB"),
    TMDB("TMDB"),
    MOVIELAB("movielab"),
    KINOPOISK_HD("kinopoisk_hd"),
    WIKIDATA("wikidata");

    private final String name;
    private Long entityId;

    public synchronized void initFromEntity(DictExternalId entity) {
        if (entity != null && this.name.equalsIgnoreCase(entity.getName())) {
            this.entityId = entity.getId();
        }
    }

    public static Optional<ExternalId> fromName(String name) {
        return Arrays.stream(values())
                .filter(source -> source.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static Optional<ExternalId> fromEntity(DictExternalId entity) {
        Optional<ExternalId> source = fromName(entity.getName());
        source.ifPresent(externalId -> externalId.initFromEntity(entity));
        return source;
    }

    public static Optional<ExternalId> fromId(Long id) {
        return Arrays.stream(values())
                .filter(source -> Objects.equals(id, source.getEntityId()))
                .findFirst();
    }

    public static Map<ExternalId, String> mapExternalIds(List<ExternalIdSeries> externalIds) {
        Map<ExternalId, String> result = new HashMap<>();
        for (ExternalIdSeries externalId: externalIds) {
            ExternalId.fromId(externalId.getExternalId().getId())
                    .ifPresent(value -> result.put(value, externalId.getValue()));
        }
        return result;
    }
}
