package io.github.nikoir.seriesparser.mapper;

import io.github.nikoir.seriesparser.domain.entity.Series;
import io.github.nikoir.seriesparser.dto.response.SeriesViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface SeriesMapper {

    @Mapping(target="year", source="releaseDate")
    SeriesViewDto toViewDto(Series series);

    List<SeriesViewDto> toViewDtoList(List<Series> seriesList);

    default Integer mapReleaseDateToYear(LocalDate releaseDate) {
        return Optional
                .ofNullable(releaseDate)
                .map(LocalDate::getYear)
                .orElse(null);
    }
}
