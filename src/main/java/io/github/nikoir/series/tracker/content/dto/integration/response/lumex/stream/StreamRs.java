package io.github.nikoir.series.tracker.content.dto.integration.response.lumex.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.nikoir.series.tracker.content.dto.integration.response.lumex.ContentType;
import io.github.nikoir.series.tracker.content.serialization.ContentTypeDeserializer;

import java.util.List;

public record StreamRs(
        @JsonProperty("ads")
        Ad ads,

        @JsonProperty("player")
        Player player
) {
        public record Ad(
                @JsonProperty("rolls")
                List<AdRoll> rolls,

                @JsonProperty("banners")
                BannerSettings banners
        ) {}

        public record AdRoll(
                @JsonProperty("tag_url")
                String tagUrl,

                @JsonProperty("time_offset")
                String timeOffset
        ) {}

        public record BannerSettings(
                @JsonProperty("pausebanner")
                Boolean pauseBanner,

                @JsonProperty("endtag")
                Boolean endTag
        ) {}

        public record Episode(
                @JsonProperty("episode_id")
                Integer episodeId,

                @JsonProperty("name")
                String name,

                @JsonProperty("poster")
                String poster,

                @JsonProperty("media")
                List<Translation> media
        ) {}

        public record Player(
                @JsonProperty("content_type")
                @JsonDeserialize(using = ContentTypeDeserializer.class)
                ContentType contentType,

                @JsonProperty("content_id")
                Integer contentId,

                @JsonProperty("kinopoisk_id")
                Integer kinopoiskId,

                @JsonProperty("poster")
                String poster,

                @JsonProperty("media")
                List<Season> media
        ) {}

        public record Season(
                @JsonProperty("season_id")
                Integer seasonId,

                @JsonProperty("season_name")
                String seasonName,

                @JsonProperty("episodes")
                List<Episode> episodes
        ) {}

        public record Track(
                @JsonProperty("kind")
                String kind,

                @JsonProperty("src")
                String src,

                @JsonProperty("srlang")
                String language,

                @JsonProperty("label")
                String label
        ) {}

        public record Translation(
                @JsonProperty("translation_id")
                Integer translationId,

                @JsonProperty("translation_name")
                String translationName,

                @JsonProperty("playlist")
                String playlist,

                @JsonProperty("tracks")
                List<Track> tracks,

                @JsonProperty("max_quality")
                Integer maxQuality
        ) {}
}