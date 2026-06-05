package io.github.nikoir.series.tracker.telegram.command.handler.inline;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSubscriptionRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.content.facade.SeriesSubscribeFacade;
import io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum;
import io.github.nikoir.series.tracker.telegram.command.handler.base.BaseInlineCommand;
import io.github.nikoir.series.tracker.telegram.service.SeriesSendService;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.List;

import static io.github.nikoir.series.tracker.telegram.command.enums.InlineCommandEnum.SUBSCRIPTIONS;

@Component
public class SubscriptionsInlineCommand extends BaseInlineCommand {
    private final SeriesSubscribeFacade subscribeFacade;

    public SubscriptionsInlineCommand(TelegramService telegramService,
                                      SeriesSendService seriesSendService,
                                      SeriesSubscribeFacade subscribeFacade,
                                      UserSessionService userSessionService) {
        super(telegramService, userSessionService, seriesSendService);
        this.subscribeFacade = subscribeFacade;
    }

    @Override
    public InlineCommandEnum getCommand() {
        return SUBSCRIPTIONS;
    }

    @Override
    protected void doExecute(InlineQuery inlineQuery) {
        SeriesSubscriptionRq request = createRq(inlineQuery);

        PagedModel<SeriesListViewRs> subscriptionList =  subscribeFacade.getSubscriptionList(request);

        List<String> seriesTokens = saveSeriesListToHistory(request.userTelegramId(),
                subscriptionList.getContent());

        seriesSendService.sendSeriesListInline(inlineQuery.getId(), subscriptionList, seriesTokens);
    }

    private SeriesSubscriptionRq createRq(InlineQuery inlineQuery) {
        int page = parsePageNumber(inlineQuery.getOffset());

        return new SeriesSubscriptionRq(extractChatId(inlineQuery), page, PAGE_SIZE);
    }
}
