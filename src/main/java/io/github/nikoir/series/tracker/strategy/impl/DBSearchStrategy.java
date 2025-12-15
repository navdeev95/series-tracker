package io.github.nikoir.series.tracker.strategy.impl;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.adapter.series.shorts.DatabaseSeriesShortAdapter;
import io.github.nikoir.series.tracker.strategy.SeriesSearchStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DBSearchStrategy implements SeriesSearchStrategy {
    private final SeriesRepository seriesRepository;
    private final DatabaseSeriesShortAdapter seriesMapper;

    public PagedModel<SeriesShortViewRs> search(SeriesSearchRq request) {
        Page<Series> seriesPage = seriesRepository.searchByTitleOrEngTitle(request.title(),
                PageRequest.of(request.page(),
                request.limit()));

        return seriesMapper.toViewDtoPage(seriesPage);
    }
}
