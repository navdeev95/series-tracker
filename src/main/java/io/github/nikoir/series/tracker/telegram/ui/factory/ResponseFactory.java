package io.github.nikoir.series.tracker.telegram.ui.factory;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.telegram.model.SubscriptionStatus;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResponseFactory {
    private final InlineResultFactory inlineResultFactory;
    private final MessageFactory messageFactory;
    private final CallbackFactory callbackFactory;
    private final KeyboardFactory keyboardFactory;

    public AnswerInlineQuery createSeriesListResponse(String inlineQueryId,
                                                      PagedModel<SeriesListViewRs> series,
                                                      List<String> tokenList) {
        PagedModel<InlineQueryResult> seriesResult = inlineResultFactory.createSeriesResultInline(series, tokenList);
        return createInlineQueryAnswer(inlineQueryId, seriesResult);
    }

    public AnswerInlineQuery createSeriesNotFoundResponse(String inlineQueryId) {
        PagedModel<InlineQueryResult> noResultsPage = inlineResultFactory.createNoResultsInline();
        return createInlineQueryAnswer(inlineQueryId, noResultsPage);
    }

    public SendPhoto createSeriesDetailResponse(SeriesDetailPersonalizedRs seriesDetailViewRs,
                                                String seriesToken,
                                                Long userId) {
        return messageFactory.createSeriesDetailMessage(userId, seriesDetailViewRs, seriesToken);
    }

    public AnswerCallbackQuery createWaitingSubscribeCallbackResponse(String callbackQueryId) {
        return callbackFactory.createWaitingSubscribeCallbackAnswer(callbackQueryId);
    }

    public AnswerCallbackQuery createErrorSubscribeCallbackResponse(String callbackQueryId) {
        return callbackFactory.createErrorSubscribeCallbackAnswer(callbackQueryId);
    }

    public AnswerCallbackQuery createSuccessSubscribeCallbackResponse(String callbackQueryId) {
        return callbackFactory.createSuccessSubscribeCallbackAnswer(callbackQueryId);
    }

    public EditMessageReplyMarkup createSubscribeButtonUpdateAnswer(Long userId,
                                                                    SeriesHistoryItem historyItem,
                                                                    SubscriptionStatus subscriptionStatus) {
        InlineKeyboardMarkup keyboardMarkup = keyboardFactory.createSubscriptionKeyboard(historyItem.getToken(), subscriptionStatus);
        return createEditMarkup(userId, historyItem.getMessageId(), keyboardMarkup);
    }

    private AnswerInlineQuery createInlineQueryAnswer(String inlineQueryId,
                                                     PagedModel<InlineQueryResult> results) {
        long page = results.getMetadata().number();
        long totalPages = results.getMetadata().totalPages();

        return AnswerInlineQuery.builder()
                .inlineQueryId(inlineQueryId)
                .cacheTime(1)
                .isPersonal(true)
                .results(results.getContent())
                .nextOffset(page < totalPages - 1 ? String.valueOf(page + 1) : "")
                .build();
    }

    private EditMessageReplyMarkup createEditMarkup(Long chatId,
                                                   Integer messageId,
                                                   InlineKeyboardMarkup keyboard) {
        return EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(keyboard)
                .build();
    }
}
