package io.github.nikoir.series.tracker.content.adapter.series.detail;

import io.github.nikoir.series.tracker.common.dto.response.CountryRs;
import io.github.nikoir.series.tracker.content.domain.entity.Country;
import io.github.nikoir.series.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.dto.internal.SeriesStatus;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import org.hibernate.collection.spi.PersistentSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class DBSeriesDetailAdapter implements SeriesDetailAdapter<Series>{
    @Override
    @Mapping(target = "innerId", source = "id")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "countries", source = "countries", qualifiedByName = "mapCountries")
    @Mapping(target = "externalIds", source = "externalIds")
    @Mapping(target = "isSeries", constant = "true")
    public abstract SeriesDetailViewRs toViewDto(Series source);

    @Named("mapStatus")
    protected SeriesStatus mapStatus(Series.Status status) {
        if (status == null) {
            return null;
        }

        return switch (status) {
            case FILMING -> SeriesStatus.FILMING;
            case PRE_PRODUCTION -> SeriesStatus.PRE_PRODUCTION;
            case COMPLETED -> SeriesStatus.COMPLETED;
            case CONTINUING -> SeriesStatus.CONTINUING;
            case POST_PRODUCTION -> SeriesStatus.POST_PRODUCTION;
            case DELETED -> SeriesStatus.DELETED;
            case ANNOUNCED -> SeriesStatus.ANNOUNCED;
        };
    }

    @Named("mapCountries")
    protected List<CountryRs> mapCountries(Set<Country> countrySet) {
        if (countrySet == null) {
            return Collections.emptyList();
        }

        // Безопасная проверка на инициализацию
        if (countrySet instanceof PersistentSet<Country> persistentSet) {
            if (!persistentSet.wasInitialized()) {
                return Collections.emptyList();
            }
        }

        return countrySet.isEmpty() ?
                Collections.emptyList() :
                countrySet.stream()
                        .map(c -> new CountryRs(c.getIsoCode(), c.getName()))
                        .toList();
    }

    protected Map<ExternalId, String> mapExternalIds(List<ExternalIdSeries> externalIds) {
        return ExternalId.mapExternalIds(externalIds);
    }
}
