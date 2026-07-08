package io.github.nikoir.tracker.telegram.model.session;

import io.github.nikoir.common.dto.response.ExternalId;
import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import lombok.*;

import java.util.Map;

@ToString
@Getter
@Setter
@Builder
public class SeriesHistoryItem {
    String token;
    SeriesDetailViewRs fullSeriesDetail;
    Map<ExternalId, String> lightExternalIds;
    Integer messageId;

    public boolean hasSeriesDetails() {
        return fullSeriesDetail != null;
    }
}
