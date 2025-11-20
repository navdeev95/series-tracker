package io.github.nikoir.seriesparser.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncResult {
    int newSeasons;
    int newEpisodes;

    public void incrementNewSeasons() {
        this.newSeasons++;
    }

    public void addNewEpisodes(int count) {
        this.newEpisodes += count;
    }
}
