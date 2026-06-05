package io.github.nikoir.series.tracker.telegram.command.handler.base;
import io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import io.github.nikoir.series.tracker.telegram.command.util.CommandUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Slf4j
public abstract class BaseCallbackCommand extends BaseCommand<CallbackCommandEnum, CallbackQuery> {
    private final UserSessionService userSessionService;
    protected final SeriesSendService seriesSendService;

    public BaseCallbackCommand(TelegramService telegramService,
                               UserSessionService userSessionService,
                               SeriesSendService seriesSendService) {
        super(telegramService);
        this.userSessionService = userSessionService;
        this.seriesSendService = seriesSendService;
    }

    @Override
    protected CallbackQuery extractRequest(Update update) {
        return update.getCallbackQuery();
    }

    @Override
    protected User extractUser(CallbackQuery callbackQuery) {
        return callbackQuery.getFrom();
    }

    protected Optional<SeriesHistoryItem> getHistoryItem(CallbackQuery callbackQuery) {
        Optional<String> token = CommandUtil.extractFirstParameter(getCommand(), callbackQuery.getData());
        if (token.isPresent()) {
            return userSessionService.getHistoryItem(extractChatId(callbackQuery), token.get());
        }
        return Optional.empty();
    }

    protected void setHistoryItemMessageId(CallbackQuery callbackQuery, Integer messageId) {
        Optional<String> token = CommandUtil.extractFirstParameter(getCommand(), callbackQuery.getData());
        token.ifPresent(string -> userSessionService.setHistoryItemMessageId(extractChatId(callbackQuery), string, messageId));
    }

    protected void handleMissingHistoryItem(CallbackQuery callbackQuery) {
        telegramService.sendErrorMessage(extractChatId(callbackQuery));
    }

    protected void sendWaitingState(CallbackQuery query, SeriesHistoryItem historyItem) {
        seriesSendService.sendWaitingSubscribeAnswer(query.getId());
        seriesSendService.setWaitingButton(query.getFrom().getId(), historyItem);
    }

    protected void sendSubscribedState(CallbackQuery query, SeriesHistoryItem historyItem) {
        seriesSendService.sendSuccessSubscribeAnswer(query.getId());
        seriesSendService.setSubscribedButton(query.getFrom().getId(), historyItem);
    }

    protected void sendUnsubscribedState(CallbackQuery query, SeriesHistoryItem historyItem) {
        seriesSendService.sendErrorSubscribeAnswer(query.getId());
        seriesSendService.setUnsubscribedButton(query.getFrom().getId(), historyItem);
    }
}
