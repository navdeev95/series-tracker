package io.github.nikoir.series.tracker.dto.api.response.kinopoisk;

import io.github.nikoir.series.tracker.enums.ExternalId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KinopoiskExternalId {
    IMDB("imdb", ExternalId.IMDB),
    TMDB("tmdb", ExternalId.TMDB),
    KINOPOISK_HD("kpHD", ExternalId.KINOPOISK_HD);
    private final String name;
    private final ExternalId externalId;
}
