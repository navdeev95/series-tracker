package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.dto.internal.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.mapper.SeriesDetailMapper;
import io.github.nikoir.series.tracker.strategy.context.SeriesGetStrategyContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesCreateService {
    private final SeriesGetStrategyContext seriesGetStrategyContext;
    private final SeriesDetailMapper detailMapper;
    private final SeriesRepository seriesRepository;
    private final ExternalIdRepository externalIdRepository;

    @Transactional
    public Series create(Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesDetailView = seriesGetStrategyContext.get(externalIds);

        Series entity = detailMapper.toEntity(seriesDetailView);
        entity.setExternalIds(mapExternalIds(entity, externalIds));
        return seriesRepository.save(entity);
    }
    
    private List<ExternalIdSeries> mapExternalIds(Series entity, Map<ExternalId, String> externalIds) {
        List<ExternalIdSeries> result = new LinkedList<>();
        for (ExternalId externalId: externalIds.keySet()) {
            String value = externalIds.get(externalId);
            result.add(ExternalIdSeries.builder()
                            .value(value)
                            .externalId(externalIdRepository.getReferenceById(externalId.getEntityId()))
                            .series(entity)
                    .build());
        }

        return result;
    }
}
