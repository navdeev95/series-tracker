package io.github.nikoir.series.tracker.enums;

import io.github.nikoir.series.tracker.domain.entity.dictionary.DictExternalId;
import io.github.nikoir.series.tracker.domain.entity.dictionary.DictSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum ExternalId {
    KINOPOISK("kinopoisk"),
    IMDB("IMDB"),
    TMDB("TMDB"),
    MOVIELAB("movielab"),
    KINOPOISK_HD("kinopoisk_hd");

    private final String name;
    private Long entityId;

    public synchronized void initFromEntity(DictExternalId entity) {
        if (entity != null && this.name.equalsIgnoreCase(entity.getName())) {
            this.entityId = entity.getId();
        }
    }

    public static ExternalId fromName(String name) {
        return Arrays.stream(values())
                .filter(source -> source.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown external id: " + name));
    }

    public static ExternalId fromEntity(DictExternalId entity) {
        ExternalId source = fromName(entity.getName());
        source.initFromEntity(entity);
        return source;
    }

    public static ExternalId fromId(Long id) {
        return Arrays.stream(values())
                .filter(source -> Objects.equals(id, source.getEntityId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown external id: " + id));
    }
}
