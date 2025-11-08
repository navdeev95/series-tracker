package io.github.nikoir.seriesparser.service.series.search;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.domain.repo.SeriesRepository;
import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.mapper.SeriesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DBSearchStrategy implements SeriesSearchStrategy {
    private final SeriesRepository seriesRepository;
    private final SeriesMapper seriesMapper;

    public PagedModel<SeriesViewRs> search(SeriesSearchRq request) {
        Page<Series> seriesPage = seriesRepository.searchByTitleOrEngTitle(request.title(),
                PageRequest.of(request.page(),
                request.limit()));

        return seriesMapper.toViewDtoPage(seriesPage);
    }
}
