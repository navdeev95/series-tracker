package io.github.nikoir.series.tracker.telegram.command.handler.callback;

import io.github.nikoir.series.tracker.content.facade.SeriesPersonalInfoFacade;
import io.github.nikoir.series.tracker.content.facade.SeriesSubscribeFacade;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseCallbackCommand;
import io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Optional;

@Component
public class SeriesSubscribeCallbackCommand extends BaseCallbackCommand {
    private final SeriesSubscribeFacade seriesSubscribeFacade;

    public SeriesSubscribeCallbackCommand(SeriesSendService seriesSendService,
                                          SeriesSubscribeFacade seriesSubscribeFacade,
                                          TelegramService telegramService,
                                          UserSessionService userSessionService,
                                          SeriesPersonalInfoFacade seriesPersonalInfoFacade) {
        super(telegramService, userSessionService, seriesSendService, seriesPersonalInfoFacade);
        this.seriesSubscribeFacade = seriesSubscribeFacade;
    }

    @Override
    public CallbackCommandEnum getCommand() {
        return CallbackCommandEnum.SUBSCRIBE;
    }

    @Override
    protected void doExecute(CallbackQuery callbackQuery) {
        Optional<SeriesHistoryItem> historyItemOptional = getHistoryItem(callbackQuery);
        if (historyItemOptional.isEmpty()) {
            handleMissingHistoryItem(callbackQuery);
            return;
        }

        SeriesHistoryItem historyItem = historyItemOptional.get();
        processSubscription(callbackQuery, historyItem);
    }

    private void processSubscription(CallbackQuery callbackQuery, SeriesHistoryItem historyItem) {
        if (!historyItem.hasSeriesDetails()) {
            handleError(callbackQuery);
            return;
        }
        sendWaitingState(callbackQuery, historyItem);
        try {
            Long chatId = extractChatId(callbackQuery);
            seriesSubscribeFacade.subscribeIfNotSubscribed(historyItem.getFullSeriesDetail(), chatId);

            sendSubscribedState(callbackQuery, historyItem);
        } catch (Exception ex) {
            sendUnsubscribedState(callbackQuery, historyItem);
        }
    }
}
