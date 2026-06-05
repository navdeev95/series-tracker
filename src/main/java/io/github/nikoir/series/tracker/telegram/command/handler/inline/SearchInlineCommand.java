package io.github.nikoir.series.tracker.telegram.command.handler.inline;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesSearchRs;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.context.SeriesSearchStrategyContext;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseInlineCommand;
import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.UserSession;
import io.github.nikoir.series.tracker.telegram.model.session.UserStateEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SearchContext;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.List;
import java.util.Optional;

import static io.github.nikoir.series.tracker.telegram.command.util.CommandUtil.extractFirstParameter;

@Slf4j
@Component
public class SearchInlineCommand extends BaseInlineCommand {
    private final SeriesSearchStrategyContext searchStrategyContext;

    public SearchInlineCommand(TelegramService telegramService,
                               SeriesSendService seriesSendService,
                               SeriesSearchStrategyContext searchStrategyContext,
                               UserSessionService userSessionService) {
        super(telegramService, userSessionService, seriesSendService);
        this.searchStrategyContext = searchStrategyContext;
    }

    @Override
    public InlineCommandEnum getCommand() {
        return InlineCommandEnum.SEARCH;
    }

    @Override
    protected void doExecute(InlineQuery inlineQuery) {
        Long chatId = extractChatId(inlineQuery);
        userSessionService.setUserState(chatId, UserStateEnum.SEARCHING);

        Optional<String> searchText = extractSearchText(inlineQuery);
        if (searchText.isEmpty()) {
            return;
        }

        SeriesSearchRs response = performSearch(inlineQuery, searchText.get(), chatId);

        List<String> seriesTokens = saveSeriesListToHistory(chatId, response.foundSeries().getContent());

        sendResponse(inlineQuery, response, seriesTokens);
    }

    private SeriesSearchRs performSearch(InlineQuery query, String searchText, Long userId) {
        UserSession session = userSessionService.getOrCreateSession(userId);

        SeriesSearchRq searchRq = createRq(query, searchText);

        if (searchRq.page() == 0) {
            session.resetContext();
        }

        SeriesSearchRs response = executeSearch(session, searchRq);
        userSessionService.initSearchContext(userId, response.source());

        return response;
    }

    private SeriesSearchRq createRq(InlineQuery inlineQuery, String searchText) {
        int page = parsePageNumber(inlineQuery.getOffset());
        return new SeriesSearchRq(searchText, page, PAGE_SIZE);
    }

    private SeriesSearchRs executeSearch(UserSession session, SeriesSearchRq request) {
        return extractPreviousSource(session)
                .map(source -> searchStrategyContext.search(request, source))
                .orElseGet(() -> searchStrategyContext.search(request));
    }

    private Optional<Source> extractPreviousSource(UserSession session) {
        return Optional.ofNullable(session.getSearchContext())
                .map(SearchContext::getSearchSource);
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

    private Optional<String> extractSearchText(InlineQuery inlineQuery) {
        return extractFirstParameter(getCommand(), inlineQuery.getQuery());
    }
}
