package io.github.nikoir.series.tracker.content.adapter.series.detail;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;

public interface SeriesDetailAdapter<T> {
    SeriesDetailViewRs toViewDto(T source);
}
