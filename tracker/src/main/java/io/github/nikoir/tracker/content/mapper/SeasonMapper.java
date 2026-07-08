package io.github.nikoir.tracker.content.mapper;

import io.github.nikoir.tracker.content.domain.entity.Season;
import io.github.nikoir.tracker.content.dto.internal.SeasonInfo;
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
    public abstract Season toEntity(SeasonInfo seasonInfo);

    public List<Season> toEntities(List<SeasonInfo> seasonInfoList) {
        if (CollectionUtils.isEmpty(seasonInfoList)) {
            return Collections.emptyList();
        }
        return seasonInfoList
                .stream()
                .map(this::toEntity)
                .toList();
    }
}
