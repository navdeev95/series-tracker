package io.github.nikoir.series.tracker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ExternalSource {
    MOVIE_LAB("MovieLab"),
    KINOPOISK("Kinopoisk");
    private final String name;

    public static Optional<ExternalSource> findByNameIgnoreCase(String name) {
        return Arrays.stream(values())
                .filter(externalSource ->
                        StringUtils.equalsIgnoreCase(externalSource.getName(), name))
                .findFirst();
    }

}
