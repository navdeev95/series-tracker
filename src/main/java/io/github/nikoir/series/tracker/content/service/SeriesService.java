package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.content.domain.entity.Country;
import io.github.nikoir.series.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.repo.CountryRepository;
import io.github.nikoir.series.tracker.content.domain.repo.ExternalIdRepository;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.domain.repo.specification.SeriesSpecifications;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import io.github.nikoir.series.tracker.content.mapper.SeriesDetailMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final ExternalIdRepository externalIdRepository;
    private final CountryRepository countryRepository;
    private final SeriesDetailMapper detailMapper;

    @Transactional
    public Series create(SeriesDetailViewRs seriesDto) {
        Series entity = detailMapper.toEntity(seriesDto);
        entity.setExternalIds(mapExternalIds(entity, seriesDto.getExternalIds()));
        entity.setCountries(getCountriesToSave(seriesDto));
        return seriesRepository.save(entity);
    }

    public Optional<Series> find(Map<ExternalId, String> externalIds) {
        Specification<Series> seriesSpecification = SeriesSpecifications
                .hasAnyExternalIdFromList(externalIds);

        return getSeriesWithCountries(seriesSpecification);

    }

    public Page<Series> findUncompletedSeries(int page, int size) {
        return seriesRepository.searchSeriesWithoutReleases(PageRequest.of(page, size));
    }


    private Optional<Series> getSeriesWithCountries(Specification<Series> specification) {
        // 1. Получаем ID сериалов по спецификации
        List<Long> ids = seriesRepository.findAll(specification)
                .stream()
                .map(Series::getId)
                .sorted()
                .toList();

        if (ids.isEmpty()) {
            return Optional.empty();
        }

        //TODO: предусмотреть "отметку" дублей.
        // На текущий момент выбирается первый добавленный в базу сериал
        return seriesRepository.findByIdWithCountries(ids.getFirst());
    }

    private Set<Country> getCountriesToSave(SeriesDetailViewRs seriesDetails) {
        Set<Country> result = new HashSet<>();
        if (seriesDetails.getCountries() == null || seriesDetails.getCountries().isEmpty()) {
            return result;
        }

        result.addAll(seriesDetails
                .getCountries()
                .stream()
                .map(c -> countryRepository.findByIsoCode(c.isoCode()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()));
        return result;
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


