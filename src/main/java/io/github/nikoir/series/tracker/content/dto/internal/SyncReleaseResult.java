package io.github.nikoir.series.tracker.content.dto.internal;

import io.github.nikoir.series.tracker.content.domain.entity.EpisodeRelease;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SyncReleaseResult {
    @Getter
    private int newSeasonsCnt;
    @Getter
    private int newEpisodesCnt;
    private final List<EpisodeRelease> newReleases = new ArrayList<>();

    public boolean hasNewContent() {
        return newEpisodesCnt > 0;
    }

    public void addNewSeason() {
        this.newSeasonsCnt++;
    }

    public void addNewEpisodes(int episodesCnt) {
        this.newEpisodesCnt += episodesCnt;
    }

    public static SyncReleaseResult empty() {
        return new SyncReleaseResult();
    }


}
