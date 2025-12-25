package io.github.nikoir.series.tracker.telegram.handler.impl;

import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.strategy.context.SeriesSearchStrategyContext;
import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import io.github.nikoir.series.tracker.telegram.dto.TelegramMessage;
import io.github.nikoir.series.tracker.telegram.handler.Command;
import io.github.nikoir.series.tracker.telegram.model.BotCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.UserStateEnum;
import io.github.nikoir.series.tracker.telegram.service.MediaGroupService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import io.github.nikoir.series.tracker.telegram.util.CommandUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchCommand implements Command {
    private final SeriesSearchStrategyContext searchStrategyContext;
    private final UserSessionService userSessionService;
    private final MediaGroupService mediaGroupService;
    private final SeriesNotificationBot bot;

    @Override
    public BotCommandEnum getCommand() {
        return BotCommandEnum.SEARCH;
    }

    @Override
    public void execute(TelegramMessage message) {
        Optional<String> title;
        if (CommandUtil.isCommand(message.text())) {
            title = CommandUtil.extractParameterString(message.text());
        } else {
            title = Optional.ofNullable(message.text());
        }
        if (title.isEmpty()) {
            userSessionService.setUserState(message.userId(),
                    UserStateEnum.AWAITING_SEARCH_QUERY);
            bot.sendTextMessage(message.chatId(), "Введите название сериала.");
            return;
        }

        SeriesSearchRq searchRq = new SeriesSearchRq(title.get(), 1, 10);

        PagedModel<SeriesShortViewRs> result = searchStrategyContext.search(searchRq);
        if (CollectionUtils.isEmpty(result.getContent())) {
            bot.sendTextMessage(message.chatId(), "Ничего не найдено. Пожалуйста выпоните поиск заново с другим запросом.");
            userSessionService.clearUserState(message.userId());
            return;
        }
        mediaGroupService.sendSeriesCarousel(message.chatId(),
                result.getContent(),
                title.get());
        userSessionService.clearUserState(message.userId());
    }
}
