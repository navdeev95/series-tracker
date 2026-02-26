package io.github.nikoir.series.tracker.telegram.command.handler.callback;

import io.github.nikoir.series.tracker.facade.SeriesSubscribeFacade;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseCallbackCommand;
import io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
public class SeriesSubscribeCallbackCommand extends BaseCallbackCommand {
    private final SeriesSendService seriesSendService;
    private final SeriesSubscribeFacade seriesSubscribeFacade;

    public SeriesSubscribeCallbackCommand(SeriesSendService seriesSendService,
                                          SeriesSubscribeFacade seriesSubscribeFacade,
                                          TelegramService telegramService,
                                          UserSessionService userSessionService) {
        super(telegramService, userSessionService);
        this.seriesSendService = seriesSendService;
        this.seriesSubscribeFacade = seriesSubscribeFacade;
    }

    @Override
    public CallbackCommandEnum getCommand() {
        return CallbackCommandEnum.SUBSCRIBE;
    }

    @Override
    protected void innerExecute(CallbackQuery callbackQuery) {
        User user = callbackQuery.getFrom();

        Optional<SeriesHistoryItem> historyItemOptional = getHistoryItem(callbackQuery);
        if (historyItemOptional.isEmpty()) {
            handleMissingHistoryItem(user);
            return;
        }

        SeriesHistoryItem historyItem = historyItemOptional.get();
        processSubscription(callbackQuery, user, historyItem);
    }

    private void processSubscription(CallbackQuery callbackQuery, User user, SeriesHistoryItem historyItem) {
        sendWaitingState(callbackQuery, historyItem);
        try {
            executeSubscription(user, historyItem);
            sendSuccessState(callbackQuery, historyItem);
        } catch (Exception ex) {
            sendErrorState(callbackQuery, historyItem);
        }
    }

    private void executeSubscription(User user, SeriesHistoryItem historyItem) {
        seriesSubscribeFacade.subscribe(user.getId(), historyItem.getExternalIds());
    }

    private void sendWaitingState(CallbackQuery query, SeriesHistoryItem historyItem) {
        seriesSendService.sendWaitingSubscribeAnswer(query.getId());
        seriesSendService.setWaitingButton(query.getFrom().getId(), historyItem);
    }

    private void sendSuccessState(CallbackQuery query, SeriesHistoryItem historyItem) {
        seriesSendService.sendSuccessSubscribeAnswer(query.getId());
        seriesSendService.setSubscribedButton(query.getFrom().getId(), historyItem);
    }

    private void sendErrorState(CallbackQuery query, SeriesHistoryItem historyItem) {
        seriesSendService.sendErrorSubscribeAnswer(query.getId());
        seriesSendService.setUnsubscribedButton(query.getFrom().getId(), historyItem);
    }
}
