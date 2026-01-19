package io.github.nikoir.series.tracker.telegram.handler.inline;

import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.strategy.context.SeriesSearchStrategyContext;
import io.github.nikoir.series.tracker.telegram.handler.BaseHandler;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

@Slf4j
@Component
@RequiredArgsConstructor
public class InlineQueryHandler extends BaseHandler {
    private final SeriesSendService seriesSendService;
    private final SeriesSearchStrategyContext searchStrategyContext;

    @Override
    public void handle(Update update) {
        InlineQuery inlineQuery = update.getInlineQuery();
        PagedModel<SeriesShortViewRs> series = findSeries(inlineQuery);
        seriesSendService.sendSeriesInline(inlineQuery.getId(), series);
    }

    private PagedModel<SeriesShortViewRs> findSeries(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        String offset = inlineQuery.getOffset();
        int page = 0;

        // Парсим offset для определения страницы
        if (!StringUtils.isEmpty(offset)) {
            try {
                page = Integer.parseInt(offset);
            } catch (NumberFormatException ignored) {
            }
        }

        int pageSize = 10;

        return searchStrategyContext.search(new SeriesSearchRq(query, page, pageSize));
    }
}
