package io.github.nikoir.series.tracker.telegram.command.handler.callback;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.facade.SeriesGetFacade;
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

import static io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum.SERIES_DETAIL;

@Component
public class SeriesDetailCallbackCommand extends BaseCallbackCommand {
    private final SeriesGetFacade seriesGetFacade;

    public SeriesDetailCallbackCommand(SeriesGetFacade seriesGetFacade,
                                       SeriesSendService seriesSendService,
                                       TelegramService telegramService,
                                       UserSessionService userSessionService) {
        super(telegramService, userSessionService, seriesSendService);
        this.seriesGetFacade = seriesGetFacade;
    }

    @Override
    public CallbackCommandEnum getCommand() {
        return SERIES_DETAIL;
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

        SeriesDetailPersonalizedRs seriesDetailViewRs = seriesGetFacade
                .getSeriesInfoForUser(user.getId(), historyItem.getExternalIds());

        Optional<Integer> sentMessageId = seriesSendService.sendSeriesDetailInfo(seriesDetailViewRs,
                historyItem.getToken(),
                user.getId());

        sentMessageId.ifPresent(messageId -> setHistoryItemMessageId(callbackQuery, messageId));
    }

}
