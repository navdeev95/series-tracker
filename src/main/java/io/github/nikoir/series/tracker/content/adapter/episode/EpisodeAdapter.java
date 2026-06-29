package io.github.nikoir.series.tracker.content.adapter.episode;

import io.github.nikoir.series.tracker.content.dto.internal.EpisodeInfo;
import io.github.nikoir.series.tracker.content.dto.internal.SeasonInfo;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public interface EpisodeAdapter<Rq> {
    EpisodeInfo toEpisodeInfo(Rq source);

    default List<EpisodeInfo> toEpisodeInfoList(List<Rq> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(this::toEpisodeInfo)
                .toList();
    }
}
