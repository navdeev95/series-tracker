package io.github.nikoir.series.tracker.telegram.command.handler.base;

import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseInlineCommand extends BaseCommand<InlineCommandEnum> {
    protected final UserSessionService userSessionService;
    protected final SeriesSendService seriesSendService;

    protected static final int PAGE_SIZE = 10;

    @Override
    public void execute(Update update) {
        InlineQuery inlineQuery = update.getInlineQuery();
        this.innerExecute(inlineQuery);

    }

    protected abstract void innerExecute(InlineQuery inlineQuery);

    protected List<String> saveSeriesListToHistory(Long userId, List<SeriesListViewRs> seriesList) {
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

    protected SeriesHistoryItem createHistoryItem(SeriesListViewRs series) {
        String token = UUID.randomUUID().toString();
        return SeriesHistoryItem.builder()
                .token(token)
                .externalIds(series.externalIds())
                .build();
    }

    protected void addHistoryItem(Long userId, SeriesHistoryItem historyItem) {
        userSessionService.addHistoryItem(userId, historyItem);
    }

    protected int parsePageNumber(String offset) {
        if (StringUtils.isEmpty(offset)) return 0;
        try {
            return Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            log.debug("Invalid page number format: {}", offset);
            return 0;
        }
    }
}
