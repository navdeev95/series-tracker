package io.github.nikoir.series.tracker.content.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TMDBEpisodeInfoRs(@JsonProperty("id")
                                Integer id,

                                @JsonProperty("name")
                                String name,

                                @JsonProperty("overview")
                                String overview,

                                @JsonProperty("air_date")
                                String airDate,

                                @JsonProperty("episode_number")
                                Integer episodeNumber,

                                @JsonProperty("episode_type")
                                String episodeType,

                                @JsonProperty("production_code")
                                String productionCode,

                                @JsonProperty("runtime")
                                Integer runtime,

                                @JsonProperty("season_number")
                                Integer seasonNumber,

                                @JsonProperty("show_id")
                                Integer showId,

                                @JsonProperty("still_path")
                                String stillPath,

                                @JsonProperty("vote_average")
                                Double voteAverage,

                                @JsonProperty("vote_count")
                                Integer voteCount) {
}
