package io.github.nikoir.series.tracker.telegram.command.handler.inline;

import io.github.nikoir.series.tracker.dto.external.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.external.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesSearchRs;
import io.github.nikoir.series.tracker.enums.Source;
import io.github.nikoir.series.tracker.strategy.context.SeriesSearchStrategyContext;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseInlineCommand;
import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.model.session.UserSession;
import io.github.nikoir.series.tracker.telegram.model.session.UserStateEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SearchContext;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.github.nikoir.series.tracker.telegram.command.util.CommandUtil.extractFirstParameter;

@RequiredArgsConstructor
@Slf4j
@Component
public class SearchInlineCommand extends BaseInlineCommand {
    private final SeriesSendService seriesSendService;
    private final SeriesSearchStrategyContext searchStrategyContext;
    private final UserSessionService userSessionService;

    private static final int PAGE_SIZE = 10;

    @Override
    public InlineCommandEnum getCommand() {
        return InlineCommandEnum.SEARCH;
    }

    @Override
    protected void innerExecute(InlineQuery inlineQuery) {
        Long userId = inlineQuery.getFrom().getId();
        userSessionService.setUserState(userId, UserStateEnum.SEARCHING);

        Optional<String> searchText = extractSearchText(inlineQuery);
        if (searchText.isEmpty()) {
            return;
        }

        SeriesSearchRs response = performSearch(inlineQuery, searchText.get(), userId);

        List<String> seriesTokens = saveSeriesListToHistory(userId, response.foundSeries().getContent());

        sendResponse(inlineQuery, response, seriesTokens);
    }

    private SeriesSearchRs performSearch(InlineQuery query, String searchText, Long userId) {
        UserSession session = userSessionService.getOrCreateSession(userId);
        int page = parsePageNumber(query.getOffset());

        if (page == 0) {
            session.resetContext();
        }

        SeriesSearchRs response = executeSearch(session, searchText, page);
        userSessionService.initSearchContext(userId, response.source());

        return response;
    }

    private SeriesSearchRs executeSearch(UserSession session, String searchText, int page) {
        SeriesSearchRq request = new SeriesSearchRq(searchText, page, PAGE_SIZE);

        return extractPreviousSource(session)
                .map(source -> searchStrategyContext.search(request, source))
                .orElseGet(() -> searchStrategyContext.search(request));
    }

    private Optional<Source> extractPreviousSource(UserSession session) {
        return Optional.ofNullable(session.getSearchContext())
                .map(SearchContext::getSearchSource);
    }

    private List<String> saveSeriesListToHistory(Long userId, List<SeriesListViewRs> seriesList) {
        List<SeriesHistoryItem> historyItemList = seriesList
                .stream()
                .map(this::createHistoryItem)
                .toList();

        historyItemList.forEach(historyItem -> addHistoryItem(userId, historyItem));

        return historyItemList
                .stream()
                .map(SeriesHistoryItem::getToken)
                .toList();
    }

    private SeriesHistoryItem createHistoryItem(SeriesListViewRs series) {
        String token = UUID.randomUUID().toString();
        return SeriesHistoryItem.builder()
                .token(token)
                .externalIds(series.externalIds())
                .build();
    }

    private void addHistoryItem(Long userId, SeriesHistoryItem historyItem) {
        userSessionService.addHistoryItem(userId, historyItem);
    }

    private void sendResponse(InlineQuery query, SeriesSearchRs response, List<String> seriesTokens) {
        if (response.isEmpty()) {
            seriesSendService.sendNotFoundInline(query.getId());
            return;
        }

        seriesSendService.sendSeriesListInline(
                query.getId(),
                response.foundSeries(),
                seriesTokens);
    }

    private int parsePageNumber(String offset) {
        if (StringUtils.isEmpty(offset)) return 0;
        try {
            return Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            log.debug("Invalid page number format: {}", offset);
            return 0;
        }
    }

    private Optional<String> extractSearchText(InlineQuery inlineQuery) {
        return extractFirstParameter(getCommand(), inlineQuery.getQuery());
    }
}
