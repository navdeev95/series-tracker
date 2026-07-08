package io.github.nikoir.tracker.content.mapper;

import io.github.nikoir.tracker.content.domain.entity.Episode;
import io.github.nikoir.tracker.content.dto.internal.EpisodeInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EpisodeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    public abstract Episode toEntity(EpisodeInfo episodeInfo);

    @Named("mapEpisodes")
    public List<Episode> toEntities(List<EpisodeInfo> episodeInfoList) {
        if (CollectionUtils.isEmpty(episodeInfoList)) {
            return Collections.emptyList();
        }

        return episodeInfoList
                .stream()
                .map(this::toEntity)
                .toList();
    }
}
