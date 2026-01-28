package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.domain.repo.specification.SeriesSpecifications;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.mapper.SeriesDetailMapper;
import io.github.nikoir.series.tracker.strategy.context.SeriesGetStrategyContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesGetStrategyContext seriesGetStrategyContext;
    private final SeriesDetailMapper detailMapper;
    private final SeriesRepository seriesRepository;
    private final ExternalIdRepository externalIdRepository;

    @Transactional
    public Series create(Map<ExternalId, String> externalIds) {
        SeriesDetailViewRs seriesDetailViewRs = seriesGetStrategyContext.get(externalIds);

        Series entity = detailMapper.toEntity(seriesDetailViewRs);
        entity.setExternalIds(mapExternalIds(entity, externalIds));
        return seriesRepository.save(entity);
    }

    public Optional<Series> find(Map<ExternalId, String> externalIds) {
        Specification<Series> seriesSpecification = SeriesSpecifications
                .hasAnyExternalIdFromList(externalIds);

        //TODO: предусмотреть "отметку" дублей.
        // На текущий момент выбирается первый добавленный в базу сериал
        List<Series> seriesList = seriesRepository
                .findAll(seriesSpecification)
                .stream()
                .sorted(Comparator.comparing(Series::getId))
                .toList();

        if (seriesList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(seriesList.getFirst());

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
