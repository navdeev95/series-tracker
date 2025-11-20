package io.github.nikoir.seriesparser.service.series.content.create;

import java.util.Map;

public interface SeriesContentCreateService {
    void createSeries(Map<String, String> externalIds);
}
