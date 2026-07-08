package io.github.nikoir.tracker.telegram.service;

import io.github.nikoir.common.dto.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.common.dto.response.SeriesListViewRs;
import io.github.nikoir.common.events.NewContentEvent;
import io.github.nikoir.tracker.telegram.model.SubscriptionStatus;
import io.github.nikoir.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.tracker.telegram.ui.factory.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;

import java.util.*;

import static io.github.nikoir.tracker.telegram.model.SubscriptionStatus.NOT_SUBSCRIBED;
import static io.github.nikoir.tracker.telegram.model.SubscriptionStatus.SUBSCRIBED;
import static io.github.nikoir.tracker.telegram.model.SubscriptionStatus.WAITING;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesSendService {
    private final TelegramService telegramService;
    private final ResponseFactory responseFactory;

    public void sendSeriesListInline(String inlineQueryId,
                                     PagedModel<SeriesListViewRs> series,
                                     List<String> tokenList) { //TODO: подумать, как передавать по-другому
        sendInlineAnswer(responseFactory.createSeriesListResponse(inlineQueryId, series, tokenList));
    }

    public void sendNotFoundInline(String inlineQueryId) {
        sendInlineAnswer(responseFactory.createSeriesNotFoundResponse(inlineQueryId));
    }

    public Optional<Integer> sendSeriesDetailInfo(SeriesDetailPersonalizedRs seriesDetailViewRs,
                                                  String seriesToken,
                                                  Long userId) {
        SendPhoto answer = responseFactory.createSeriesDetailResponse(seriesDetailViewRs, seriesToken, userId);
        return telegramService.execute(answer);
    }

    public void sendWaitingSubscribeAnswer(String callbackQueryId) {
        sendCallbackAnswer(responseFactory.createWaitingSubscribeCallbackResponse(callbackQueryId));
    }

    public void sendErrorSubscribeAnswer(String callbackQueryId) {
        sendCallbackAnswer(responseFactory.createErrorSubscribeCallbackResponse(callbackQueryId));
    }

    public void sendSuccessSubscribeAnswer(String callbackQueryId) {
        sendCallbackAnswer(responseFactory.createSuccessSubscribeCallbackResponse(callbackQueryId));
    }

    public void setWaitingButton(Long userId, SeriesHistoryItem historyItem) {
        updateSubscriptionButton(userId, historyItem, WAITING);
    }

    public void setSubscribedButton(Long userId, SeriesHistoryItem historyItem) {
        updateSubscriptionButton(userId, historyItem, SUBSCRIBED);
    }

    public void setUnsubscribedButton(Long userId, SeriesHistoryItem historyItem) {
        updateSubscriptionButton(userId, historyItem, NOT_SUBSCRIBED);
    }

    public void sendNewEpisodeAnswer(Long userId, NewContentEvent newContentEvent) {
        SendPhoto newEpisodeAnswer = responseFactory.createNewEpisodeAnswer(userId, newContentEvent);
        telegramService.execute(newEpisodeAnswer);
    }

    private void sendCallbackAnswer(AnswerCallbackQuery answer) {
        telegramService.execute(answer);
    }

    private void sendInlineAnswer(AnswerInlineQuery answer) {
        telegramService.execute(answer);
    }

    private void updateSubscriptionButton(Long userId,
                                          SeriesHistoryItem historyItem,
                                          SubscriptionStatus subscriptionStatus) {

        EditMessageReplyMarkup updateButtonAnswer = responseFactory.createSubscribeButtonUpdateAnswer(userId,
                historyItem,
                subscriptionStatus);

        telegramService.execute(updateButtonAnswer);
    }
}
