package io.github.nikoir.seriesparser.dto.response.lumex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum ContentType {
    TV_SERIES("tv-series", "Сериал");

    private final String apiValue;
    private final String description;

    public static ContentType fromApiValue(String apiValue) {
        if (apiValue == null) {
            return null;
        }

        for (ContentType type : ContentType.values()) {
            if (type.apiValue.equalsIgnoreCase(apiValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown content type: " + apiValue);
    }
}
