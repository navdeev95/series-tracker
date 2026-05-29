package io.github.nikoir.series.tracker.content.strategy.impl;

import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.adapter.series.shorts.DatabaseSeriesShortAdapter;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.SeriesSearchStrategy;
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

    public PagedModel<SeriesListViewRs> search(SeriesSearchRq request) {
        Page<Series> seriesPage = seriesRepository.searchByTitleOrEngTitle(request.title(),
                PageRequest.of(request.page(),
                request.limit()));

        return seriesMapper.toViewDtoPage(seriesPage);
    }

    @Override
    public Source getDataSource() {
        return Source.DATABASE;
    }
}
