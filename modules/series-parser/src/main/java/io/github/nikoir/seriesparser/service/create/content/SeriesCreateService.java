package io.github.nikoir.seriesparser.service.create.content;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.domain.repo.SeriesRepository;
import io.github.nikoir.seriesparser.dto.internal.SeriesDetailViewRs;
import io.github.nikoir.seriesparser.enums.ExternalId;
import io.github.nikoir.seriesparser.enums.ExternalSource;
import io.github.nikoir.seriesparser.mapper.SeriesDetailMapper;
import io.github.nikoir.seriesparser.service.get.series.SeriesGetStrategyContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesCreateService {
    private final SeriesGetStrategyContext seriesGetStrategyContext;
    private final SeriesDetailMapper detailMapper;
    private final SeriesRepository repository;

    @Transactional
    public Series create(Map<String, String> externalIds) {
        String kinopoiskId = externalIds.get(ExternalId.KINOPOISK.getSourceName());

        SeriesDetailViewRs seriesDetailView = seriesGetStrategyContext
                .getStrategy(ExternalSource.KINOPOISK)
                .get(kinopoiskId);

        Series entity = detailMapper.toEntity(seriesDetailView);
        return repository.save(entity);
    }
}
