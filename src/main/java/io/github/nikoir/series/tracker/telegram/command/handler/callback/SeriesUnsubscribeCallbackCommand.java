package io.github.nikoir.series.tracker.telegram.command.handler.callback;

import io.github.nikoir.series.tracker.content.facade.SeriesSubscribeFacade;
import io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseCallbackCommand;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
public class SeriesUnsubscribeCallbackCommand extends BaseCallbackCommand {
    private final SeriesSubscribeFacade seriesSubscribeFacade;

    public SeriesUnsubscribeCallbackCommand(SeriesSendService seriesSendService,
                                            SeriesSubscribeFacade seriesSubscribeFacade,
                                            TelegramService telegramService,
                                            UserSessionService userSessionService) {
        super(telegramService, userSessionService, seriesSendService);
        this.seriesSubscribeFacade = seriesSubscribeFacade;
    }

    @Override
    public CallbackCommandEnum getCommand() {
        return CallbackCommandEnum.UNSUBSCRIBE;
    }

    @Override
    protected void doExecute(CallbackQuery callbackQuery) {
        Optional<SeriesHistoryItem> historyItemOptional = getHistoryItem(callbackQuery);
        if (historyItemOptional.isEmpty()) {
            handleMissingHistoryItem(callbackQuery);
            return;
        }

        SeriesHistoryItem historyItem = historyItemOptional.get();
        processUnsubscription(callbackQuery, historyItem);
    }

    private void processUnsubscription(CallbackQuery callbackQuery, SeriesHistoryItem historyItem) {
        sendWaitingState(callbackQuery, historyItem);
        try {
            executeUnsubscription(extractChatId(callbackQuery), historyItem);
            sendUnsubscribedState(callbackQuery, historyItem);
        } catch (Exception ex) {
            sendSubscribedState(callbackQuery, historyItem);
        }
    }

    private void executeUnsubscription(Long chatId, SeriesHistoryItem historyItem) {
        seriesSubscribeFacade.unsubscribe(chatId, historyItem.getExternalIds());
    }
}
