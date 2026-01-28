package io.github.nikoir.series.tracker.telegram.handler.callback;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.facade.SeriesGetFacade;
import io.github.nikoir.series.tracker.strategy.context.SeriesGetStrategyContext;
import io.github.nikoir.series.tracker.telegram.handler.BaseHandler;
import io.github.nikoir.series.tracker.telegram.model.CallbackQueryEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import io.github.nikoir.series.tracker.telegram.util.CallbackQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CallbackQueryHandler extends BaseHandler {
    private final UserSessionService userSessionService;
    private final TelegramService telegramService;
    private final SeriesGetFacade seriesGetFacade;
    private final SeriesSendService seriesSendService;
    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery =  update.getCallbackQuery();
        User user = callbackQuery.getFrom();
        Long chatId = user.getId();
        log.debug("Handling callback query. Callback data: {}", callbackQuery.getData());
        Optional<CallbackQueryEnum> query = CallbackQueryUtil.extractCallbackQuery(callbackQuery.getData());
        if (query.isEmpty()) {
            telegramService.sendUnknownCommandMessage(chatId);
            return;
        }
        String token = CallbackQueryUtil.extractParameter(callbackQuery.getData());
        Optional<SeriesHistoryItem> historyItem = userSessionService.getHistoryItem(user.getId(), token);
        if (historyItem.isEmpty()) {
            telegramService.sendErrorMessage(chatId);
            return;
        }

        SeriesDetailPersonalizedRs seriesDetailViewRs = seriesGetFacade
                .getSeriesInfoForUser(user.getId(), historyItem.get().getExternalIds());

        seriesSendService.sendSeriesDetailMessage(chatId, seriesDetailViewRs);
    }
}
