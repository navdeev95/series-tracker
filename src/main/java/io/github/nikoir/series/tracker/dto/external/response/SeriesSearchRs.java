package io.github.nikoir.series.tracker.dto.external.response;

import io.github.nikoir.series.tracker.enums.Source;
import org.springframework.data.web.PagedModel;

public record SeriesSearchRs (
        PagedModel<SeriesListViewRs> seriesList,
        Source source
) {}
