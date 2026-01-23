package io.github.nikoir.series.tracker.dto.internal;

import io.github.nikoir.series.tracker.enums.Source;
import org.springframework.data.web.PagedModel;

public record SeriesSearchRs (
        PagedModel<SeriesShortViewRs> seriesList,
        Source source
) {}
