package io.github.nikoir.series.tracker.telegram.handler.callback;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.facade.SeriesGetFacade;
import io.github.nikoir.series.tracker.facade.SeriesSubscribeFacade;
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
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CallbackQueryHandler extends BaseHandler {
    private final UserSessionService userSessionService;
    private final TelegramService telegramService;
    private final SeriesGetFacade seriesGetFacade;
    private final SeriesSubscribeFacade seriesSubscribeFacade;
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

        answerCallback(callbackQuery.getId(), "Запрос обрабатывается, подождите...⏳", false);
        switch (query.get()) {
            case SERIES_DETAIL:
                handleSeriesDetailData(callbackQuery);
                break;
            case SUBSCRIBE:
                handleSubscribeData(callbackQuery);
                break;
            default:
                telegramService.sendUnknownCommandMessage(chatId);

        }
    }

    private void handleSeriesDetailData(CallbackQuery callbackQuery) {
        User user = callbackQuery.getFrom();

        Optional<SeriesHistoryItem> historyItem = getHistoryItem(callbackQuery);
        if (historyItem.isEmpty()) {
            telegramService.sendErrorMessage(user.getId());
            return;
        }
        SeriesDetailPersonalizedRs seriesDetailViewRs = seriesGetFacade
                .getSeriesInfoForUser(user.getId(), historyItem.get().getExternalIds());

        Optional<Integer> messageId = seriesSendService.sendSeriesDetailMessage(user.getId(),
                seriesDetailViewRs,
                historyItem.get().getToken());
        messageId.ifPresent(integer -> setHistoryItemMessageId(callbackQuery, integer));
    }

    private void handleSubscribeData(CallbackQuery callbackQuery) {
        User user = callbackQuery.getFrom();

        Optional<SeriesHistoryItem> historyItemOptional = getHistoryItem(callbackQuery);
        if (historyItemOptional.isEmpty()) {
            telegramService.sendErrorMessage(user.getId());
            return;
        }
        SeriesHistoryItem historyItem = historyItemOptional.get();

        seriesSendService.updateSubscriptionButton(user.getId(),
                historyItem.getMessageId(),
                historyItem.getToken(),
                SeriesSendService.SubscriptionStatus.WAITING);
        try {
            seriesSubscribeFacade.subscribe(user.getId(), historyItemOptional.get().getExternalIds());
        } catch (Exception ex) {
            answerCallback(callbackQuery.getId(), "Произошла ошибка при обработке запроса", false);
            seriesSendService.updateSubscriptionButton(user.getId(),
                    historyItem.getMessageId(),
                    historyItem.getToken(),
                    SeriesSendService.SubscriptionStatus.NOT_SUBSCRIBED);
            return;
        }

        seriesSendService.updateSubscriptionButton(user.getId(),
                historyItem.getMessageId(),
                historyItem.getToken(),
                SeriesSendService.SubscriptionStatus.SUBSCRIBED);
    }

    private Optional<SeriesHistoryItem> getHistoryItem(CallbackQuery callbackQuery) {
        String token = CallbackQueryUtil.extractParameter(callbackQuery.getData());
        return userSessionService.getHistoryItem(callbackQuery.getFrom().getId(), token);
    }

    private void setHistoryItemMessageId(CallbackQuery callbackQuery, Integer messageId) {
        String token = CallbackQueryUtil.extractParameter(callbackQuery.getData());
        userSessionService.setHistoryItemMessageId(callbackQuery.getFrom().getId(), token, messageId);
    }

    private void answerCallback(String callbackQueryId, String text, boolean showAlert) {
        try {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .text(text)
                    .showAlert(showAlert)
                    .build();

            telegramService.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Failed to answer callback", e);
        }
    }
}
