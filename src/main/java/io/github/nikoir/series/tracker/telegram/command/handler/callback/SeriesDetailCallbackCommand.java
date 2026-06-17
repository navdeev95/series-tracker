package io.github.nikoir.series.tracker.telegram.command.handler.callback;

import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.content.facade.SeriesPersonalInfoFacade;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseCallbackCommand;
import io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Optional;

import static io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum.SERIES_DETAIL;

@Component
public class SeriesDetailCallbackCommand extends BaseCallbackCommand {
    public SeriesDetailCallbackCommand(SeriesPersonalInfoFacade seriesPersonalInfoFacade,
                                       SeriesSendService seriesSendService,
                                       TelegramService telegramService,
                                       UserSessionService userSessionService) {
        super(telegramService, userSessionService, seriesSendService, seriesPersonalInfoFacade);
    }

    @Override
    public CallbackCommandEnum getCommand() {
        return SERIES_DETAIL;
    }

    @Override
    protected void doExecute(CallbackQuery callbackQuery) {
        Optional<SeriesHistoryItem> historyItemOptional = getHistoryItem(callbackQuery);
        if (historyItemOptional.isEmpty()) {
            handleMissingHistoryItem(callbackQuery);
            return;
        }

        SeriesHistoryItem historyItem = historyItemOptional.get();
        Optional<SeriesDetailPersonalizedRs> personalizedRs = getPersonalizedSeriesData(callbackQuery, historyItem);
        if (personalizedRs.isEmpty()) {
            handleError(callbackQuery);
            return;
        }
        sendAndSaveMessageId(historyItem, callbackQuery, personalizedRs.get());
    }

    private void sendAndSaveMessageId(SeriesHistoryItem historyItem,
                                      CallbackQuery callbackQuery,
                                      SeriesDetailPersonalizedRs personalizedRs) {
        Optional<Integer> messageIdOptional = seriesSendService.sendSeriesDetailInfo(personalizedRs,
                historyItem.getToken(),
                extractChatId(callbackQuery));

        if (messageIdOptional.isEmpty()) {
            return;
        }

        setHistoryItemMessageId(callbackQuery, messageIdOptional.get());
    }

}
