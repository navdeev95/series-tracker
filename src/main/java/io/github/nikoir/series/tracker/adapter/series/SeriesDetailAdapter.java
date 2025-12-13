package io.github.nikoir.series.tracker.adapter.series;

import io.github.nikoir.series.tracker.dto.internal.SeriesDetailViewRs;

public interface SeriesDetailAdapter<T> {
    SeriesDetailViewRs toViewDto(T source);
}
