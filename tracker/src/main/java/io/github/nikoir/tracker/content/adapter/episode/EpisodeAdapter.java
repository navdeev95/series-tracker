package io.github.nikoir.tracker.content.adapter.episode;

import io.github.nikoir.tracker.content.dto.internal.EpisodeInfo;
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
