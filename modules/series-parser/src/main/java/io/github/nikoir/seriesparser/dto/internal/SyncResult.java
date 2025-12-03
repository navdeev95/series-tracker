package io.github.nikoir.seriesparser.dto.internal;

import io.github.nikoir.seriesparser.domain.entity.EpisodeRelease;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SyncResult {
    @Getter
    private int newSeasonsCnt;
    @Getter
    private int newEpisodesCnt;
    private final List<EpisodeRelease> newReleases = new ArrayList<>();

    public List<EpisodeRelease> getNewReleases() {
        return List.copyOf(newReleases);
    }

    public int getNewReleasesCnt() {
        return newReleases.size();
    }

    public boolean hasNewContent() {
        return !newReleases.isEmpty();
    }

    public void addNewReleases(List<EpisodeRelease> episodeReleases) {
        this.newReleases.addAll(episodeReleases);
    }

    public void addNewSeason() {
        this.newSeasonsCnt++;
    }

    public void addNewEpisode() {
        this.newEpisodesCnt++;
    }

    public void addNewEpisodes(int episodesCnt) {
        this.newEpisodesCnt += episodesCnt;
    }

    public static SyncResult empty() {
        return new SyncResult();
    }
}
