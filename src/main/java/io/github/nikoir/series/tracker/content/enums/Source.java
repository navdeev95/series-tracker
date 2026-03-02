package io.github.nikoir.series.tracker.content.enums;

import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Source {
    DATABASE("DataBase", false),
    KINOPOISK("KinoPoisk", true),
    MOVIELAB("MovieLab", true);

    private final String name;
    private final boolean external;

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

}
