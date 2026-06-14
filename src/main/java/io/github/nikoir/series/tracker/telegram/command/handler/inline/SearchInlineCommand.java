package io.github.nikoir.series.tracker.telegram.command.handler.inline;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.facade.SeriesSearchFacade;
import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseInlineCommand;
import io.github.nikoir.series.tracker.telegram.model.session.UserStateEnum;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.List;
import java.util.Optional;

import static io.github.nikoir.series.tracker.telegram.command.util.CommandUtil.extractFirstParameter;

@Slf4j
@Component
public class SearchInlineCommand extends BaseInlineCommand {
    private final SeriesSearchFacade searchFacade;

    public SearchInlineCommand(TelegramService telegramService,
                               SeriesSendService seriesSendService,
                               SeriesSearchFacade seriesSearchFacade,
                               UserSessionService userSessionService) {
        super(telegramService, userSessionService, seriesSendService);
        this.searchFacade = seriesSearchFacade;
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

        PagedModel<SeriesListViewRs> response = searchFacade.search(createRq(inlineQuery, searchText.get()));

        List<String> seriesTokens = saveSeriesListToHistory(chatId, response.getContent());

        sendResponse(inlineQuery, response, seriesTokens);
    }

    private SeriesSearchRq createRq(InlineQuery inlineQuery, String searchText) {
        int page = parsePageNumber(inlineQuery.getOffset());
        return new SeriesSearchRq(searchText, page, PAGE_SIZE);
    }

    private void sendResponse(InlineQuery query, PagedModel<SeriesListViewRs> response, List<String> seriesTokens) {
        if (response.getContent().isEmpty()) {
            seriesSendService.sendNotFoundInline(query.getId());
            return;
        }

        seriesSendService.sendSeriesListInline(
                query.getId(),
                response,
                seriesTokens);
    }

    private Optional<String> extractSearchText(InlineQuery inlineQuery) {
        return extractFirstParameter(getCommand(), inlineQuery.getQuery());
    }
}
