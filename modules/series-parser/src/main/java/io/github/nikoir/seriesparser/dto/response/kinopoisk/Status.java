package io.github.nikoir.seriesparser.dto.response.kinopoisk;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    FILMING("filming"),
    PRE_PRODUCTION("pre-production"),
    COMPLETED("completed"),
    ANNOUNCED("announced"),
    POST_PRODUCTION("post-production");

    private final String name;

    public static Status fromApiValue(String apiValue) {
        if (apiValue == null) {
            return null;
        }

        for (Status status : Status.values()) {
            if (status.name.equals(apiValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown content type: " + apiValue);
    }
}
