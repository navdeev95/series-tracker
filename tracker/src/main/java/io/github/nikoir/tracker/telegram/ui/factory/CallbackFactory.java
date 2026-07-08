package io.github.nikoir.tracker.telegram.ui.factory;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

@Component
public class CallbackFactory {
    public AnswerCallbackQuery createWaitingSubscribeCallbackAnswer(String callbackQueryId) {
        return createAnswerCallbackQuery(callbackQueryId, "Подождите, подписка оформляется... ⏳");
    }

    public AnswerCallbackQuery createErrorSubscribeCallbackAnswer(String callbackQueryId) {
        return createAnswerCallbackQuery(callbackQueryId, "Произошла ошибка при обработке запроса 😔");
    }

    public AnswerCallbackQuery createSuccessSubscribeCallbackAnswer(String callbackQueryId) {
        return createAnswerCallbackQuery(callbackQueryId, "Подписка оформлена ✅");
    }

    private AnswerCallbackQuery createAnswerCallbackQuery(String callbackQueryId, String text) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .showAlert(false)
                .build();
    }
}
