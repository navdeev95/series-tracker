package io.github.nikoir.series.tracker.dto.integration.response.kinopoisk;

import io.github.nikoir.series.tracker.enums.ExternalId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum KinopoiskExternalId {
    IMDB("imdb", ExternalId.IMDB),
    TMDB("tmdb", ExternalId.TMDB),
    KINOPOISK_HD("kpHD", ExternalId.KINOPOISK_HD);
    private final String name;
    private final ExternalId externalId;

    public static Optional<ExternalId> getExternalIdFromName(String name) {
        return Arrays.stream(values()).filter(value ->
                StringUtils.equals(value.name, name))
                .findFirst()
                .map(KinopoiskExternalId::getExternalId);
    }
}
