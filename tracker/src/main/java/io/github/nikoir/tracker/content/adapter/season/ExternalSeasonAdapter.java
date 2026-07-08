package io.github.nikoir.tracker.content.adapter.season;

import io.github.nikoir.tracker.content.dto.internal.SeasonInfo;
import org.springframework.util.CollectionUtils;
import java.util.Collections;
import java.util.List;

public interface ExternalSeasonAdapter<Rq> {
    SeasonInfo toSeasonInfo(Rq source);

    default List<SeasonInfo> toSeasonInfoList(List<Rq> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(this::toSeasonInfo)
                .toList();
    }
}