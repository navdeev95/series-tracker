package io.github.nikoir.seriesparser.mapper;

import io.github.nikoir.seriesparser.domain.entity.Season;
import io.github.nikoir.seriesparser.dto.response.SeasonViewRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SeasonMapper {
    @Autowired
    protected EpisodeMapper episodeMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "series", ignore = true)
    @Mapping(target = "episodes", source = "episodes")
    public abstract Season toEntity(SeasonViewRs seasonViewRs);

    public List<Season> toEntities(List<SeasonViewRs> seasonViewRsList) {
        if (CollectionUtils.isEmpty(seasonViewRsList)) {
            return Collections.emptyList();
        }
        return seasonViewRsList
                .stream()
                .map(this::toEntity)
                .toList();
    }
}
