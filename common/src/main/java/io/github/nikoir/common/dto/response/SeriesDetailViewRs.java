package io.github.nikoir.common.dto.response;

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
    private List<CountryRs> countries;
    private Boolean isSeries;
    private Map<ExternalId, String> externalIds;
}
