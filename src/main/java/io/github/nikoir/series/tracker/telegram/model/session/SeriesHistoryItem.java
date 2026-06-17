package io.github.nikoir.series.tracker.telegram.model.session;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
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
