package io.github.nikoir.series.tracker.adapter.series.detail;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;

public interface SeriesDetailAdapter<T> {
    SeriesDetailViewRs toViewDto(T source);
}
