package io.github.nikoir.series.tracker.telegram.model.session;

import io.github.nikoir.series.tracker.enums.ExternalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@AllArgsConstructor
@ToString
@Getter
public class SeriesHistoryItem {
    String token;
    Map<ExternalId, String> externalIds;
}
