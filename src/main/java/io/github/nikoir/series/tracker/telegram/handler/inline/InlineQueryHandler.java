package io.github.nikoir.series.tracker.telegram.handler.inline;

import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesSearchRs;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.enums.Source;
import io.github.nikoir.series.tracker.strategy.context.SeriesSearchStrategyContext;
import io.github.nikoir.series.tracker.telegram.handler.BaseHandler;
import io.github.nikoir.series.tracker.telegram.model.session.UserSession;
import io.github.nikoir.series.tracker.telegram.model.session.UserStateEnum;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
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
    private final UserSessionService userSessionService;

    @Override
    public void handle(Update update) {
        InlineQuery inlineQuery = update.getInlineQuery();

        userSessionService.setUserState(inlineQuery.getFrom().getId(),
                UserStateEnum.SEARCHING);

        if (StringUtils.isBlank(inlineQuery.getQuery()) ||
                StringUtils.isEmpty(inlineQuery.getQuery())) {
            return;
        }
        PagedModel<SeriesShortViewRs> series = findSeries(inlineQuery);
        seriesSendService.sendSeriesListInline(inlineQuery.getId(),
                inlineQuery.getFrom().getId(),
                series);
    }

    private PagedModel<SeriesShortViewRs> findSeries(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        String offset = inlineQuery.getOffset();

        UserSession session = userSessionService.getOrCreateSession(inlineQuery.getFrom().getId());
        int page = 0;

        // Парсим offset для определения страницы
        if (!StringUtils.isEmpty(offset)) {
            try {
                page = Integer.parseInt(offset);
            } catch (NumberFormatException ignored) {
            }
        }

        log.debug("page = {}", page);

        if (page == 0) {
            log.debug("reset context");
            session.resetContext();
        }

        int pageSize = 10;
        Source searchSource = null;
        if (session.getSearchContext() != null) {
            searchSource = session.getSearchContext().getSearchSource();
            log.debug("get search source: {}", searchSource);
        }

        SeriesSearchRs response = searchStrategyContext.search(new SeriesSearchRq(query, page, pageSize), searchSource);

        userSessionService.initSearchContext(inlineQuery.getFrom().getId(), response.source());
        log.debug("reset search context. set search source: {}", response.source());

        return response.seriesList();
    }
}
