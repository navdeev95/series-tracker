package io.github.nikoir.common.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    @Setter
    private Long entityId;

    public static Optional<ExternalId> fromName(String name) {
        return Arrays.stream(values())
                .filter(source -> source.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static Optional<ExternalId> fromId(Long id) {
        return Arrays.stream(values())
                .filter(source -> Objects.equals(id, source.getEntityId()))
                .findFirst();
    }
}
