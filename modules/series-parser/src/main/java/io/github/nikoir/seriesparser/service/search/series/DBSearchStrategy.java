package io.github.nikoir.seriesparser.service.search.series;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.domain.repo.SeriesRepository;
import io.github.nikoir.seriesparser.dto.api.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.internal.SeriesShortViewRs;
import io.github.nikoir.seriesparser.adapter.series.DatabaseSeriesShortAdapter;
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
