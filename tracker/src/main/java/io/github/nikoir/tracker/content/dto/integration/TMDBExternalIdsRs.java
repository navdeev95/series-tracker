package io.github.nikoir.tracker.content.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TMDBExternalIdsRs(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("imdb_id")
        String imdbId,

        @JsonProperty("freebase_mid")
        String freebaseMid,

        @JsonProperty("freebase_id")
        String freebaseId,

        @JsonProperty("tvdb_id")
        Integer tvdbId,

        @JsonProperty("tvrage_id")
        Integer tvrageId,

        @JsonProperty("wikidata_id")
        String wikidataId,

        @JsonProperty("facebook_id")
        String facebookId,

        @JsonProperty("instagram_id")
        String instagramId,

        @JsonProperty("twitter_id")
        String twitterId
) {
}