package io.github.nikoir.series.tracker.adapter.season;

import io.github.nikoir.series.tracker.dto.internal.SeasonViewRs;
import org.springframework.util.CollectionUtils;
import java.util.Collections;
import java.util.List;

public interface ExternalSeasonAdapter<T> {
    SeasonViewRs toSeasonViewRs(T source);

    default List<SeasonViewRs> toSeasonViewRsList(List<T> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(this::toSeasonViewRs)
                .toList();
    }
}