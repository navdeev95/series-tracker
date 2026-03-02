package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.domain.repo.specification.SeriesSpecifications;
import io.github.nikoir.series.tracker.common.dto.request.SeriesSyncRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.mapper.SeriesDetailMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.github.nikoir.series.tracker.content.domain.entity.Series.Status.COMPLETED;
import static io.github.nikoir.series.tracker.content.domain.entity.Series.Status.DELETED;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesService {
    private final SeriesDetailMapper detailMapper;
    private final SeriesRepository seriesRepository;
    private final ExternalIdRepository externalIdRepository;

    @Transactional
    public Series create(SeriesDetailViewRs series) {
        Series entity = detailMapper.toEntity(series);
        entity.setExternalIds(mapExternalIds(entity, series.externalIds()));
        try {
            return seriesRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            log.error("Series entity already exists", ex);
            return find(series.externalIds()).orElseThrow();
        }

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

    public Page<Series> findUncompletedSeries(SeriesSyncRq syncRq) {
        return seriesRepository.searchSeriesWithStatus(true,
                List.of(COMPLETED, DELETED),
                PageRequest.of(syncRq.page(), syncRq.limit()));
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
