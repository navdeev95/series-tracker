package io.github.nikoir.series.tracker.content.mapper;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.common.dto.response.SeasonViewRs;
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
    public abstract Episode toEntity(SeasonViewRs.EpisodeViewRs episodeViewRs);

    @Named("mapEpisodes")
    public List<Episode> toEntities(List<SeasonViewRs.EpisodeViewRs> episodeViewRsList) {
        if (CollectionUtils.isEmpty(episodeViewRsList)) {
            return Collections.emptyList();
        }

        return episodeViewRsList
                .stream()
                .map(this::toEntity)
                .toList();
    }
}
