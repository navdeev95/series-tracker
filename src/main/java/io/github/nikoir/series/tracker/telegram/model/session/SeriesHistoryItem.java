package io.github.nikoir.series.tracker.telegram.model.session;

import io.github.nikoir.series.tracker.enums.ExternalId;
import lombok.*;

import java.util.Map;

@ToString
@Getter
@Setter
@Builder
public class SeriesHistoryItem {
    String token;
    Map<ExternalId, String> externalIds;
    Integer messageId;
}
