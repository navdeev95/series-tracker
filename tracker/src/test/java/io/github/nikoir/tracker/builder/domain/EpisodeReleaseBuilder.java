package io.github.nikoir.tracker.builder.domain;

import io.github.nikoir.tracker.content.domain.entity.Episode;
import io.github.nikoir.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.tracker.content.domain.entity.dictionary.DictSource;

import java.time.Instant;

public class EpisodeReleaseBuilder {
    private DictSource source;
    private Episode episode;

    public EpisodeReleaseBuilder withSource(DictSource source) {
        this.source = source;
        return this;
    }
    public EpisodeReleaseBuilder withEpisode(Episode episode) {
        this.episode = episode;
        return this;
    }
    public EpisodeRelease build() {
        EpisodeRelease release = new EpisodeRelease();
        release.setSource(source);
        release.setEpisode(episode);
        release.setReleaseTimestamp(Instant.now());
        return release;
    }
}
