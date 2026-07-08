package io.github.nikoir.tracker.telegram.command.util;

import io.github.nikoir.tracker.content.domain.entity.Series;

import java.util.List;

public final class SyncUtil {
    private SyncUtil() {}

    public static List<Series.Status> getActiveStatuses() {
        return List.of(
                Series.Status.FILMING,
                Series.Status.PRE_PRODUCTION,
                Series.Status.CONTINUING,
                Series.Status.ANNOUNCED
        );
    }
}
