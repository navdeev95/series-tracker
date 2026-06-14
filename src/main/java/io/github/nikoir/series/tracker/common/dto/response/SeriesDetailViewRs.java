package io.github.nikoir.series.tracker.common.dto.response;

import io.github.nikoir.series.tracker.content.dto.internal.SeriesStatus;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SeriesDetailViewRs {
    private Long innerId;
    private String title;
    private String engTitle;
    private Integer totalSeasons;
    private SeriesStatus status;
    private Integer releaseYear;
    private String posterUrl;
    private String description;
    private List<String> countries;
    private Boolean isSeries;
    private Map<ExternalId, String> externalIds;
}
