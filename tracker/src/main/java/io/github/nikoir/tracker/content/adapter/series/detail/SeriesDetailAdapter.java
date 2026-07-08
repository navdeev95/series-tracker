package io.github.nikoir.tracker.content.adapter.series.detail;

import io.github.nikoir.common.dto.response.SeriesDetailViewRs;

public interface SeriesDetailAdapter<T> {
    SeriesDetailViewRs toViewDto(T source);
}
