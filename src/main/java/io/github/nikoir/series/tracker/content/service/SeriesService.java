package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.domain.repo.specification.SeriesSpecifications;
import io.github.nikoir.series.tracker.content.dto.internal.SeriesSyncRq;
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
    private final SeriesRepository seriesRepository;
    private final ExternalIdRepository externalIdRepository;
    private final SeriesDetailMapper detailMapper;

    @Transactional
    public Series create(SeriesDetailViewRs seriesDto) {
        Series entity = detailMapper.toEntity(seriesDto);
        entity.setExternalIds(mapExternalIds(entity, seriesDto.getExternalIds()));
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
    public Page<Series> findUncompletedSeries(int page, int size) {
        return seriesRepository.searchSeriesWithStatus(
                true,
                List.of(Series.Status.COMPLETED, Series.Status.DELETED),
                PageRequest.of(page, size)
        );
    }

    private List<ExternalIdSeries> mapExternalIds(Series entity, Map<ExternalId, String> externalIds) {
        return externalIds.entrySet().stream()
                .map(entry -> ExternalIdSeries.builder()
                        .value(entry.getValue())
                        .externalId(externalIdRepository.getReferenceById(entry.getKey().getEntityId()))
                        .series(entity)
                        .build())
                .toList();
    }
}


