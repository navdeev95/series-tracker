package io.github.nikoir.series.tracker.telegram.command.handler.base;
import io.github.nikoir.series.tracker.telegram.command.enums.CallbackCommandEnum;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import io.github.nikoir.series.tracker.telegram.service.UserSessionService;
import io.github.nikoir.series.tracker.telegram.command.util.CommandUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseCallbackCommand extends BaseCommand<CallbackCommandEnum> {
    protected final TelegramService telegramService;
    private final UserSessionService userSessionService;
    @Override
    public void execute(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        this.innerExecute(callbackQuery);
    }

    protected abstract void innerExecute(CallbackQuery callbackQuery);

    protected Optional<SeriesHistoryItem> getHistoryItem(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
        Optional<String> token = CommandUtil.extractFirstParameter(getCommand(), callbackQuery.getData());
        if (token.isPresent()) {
            return userSessionService.getHistoryItem(callbackQuery.getFrom().getId(), token.get());
        }
        return Optional.empty();
    }

    protected void setHistoryItemMessageId(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery, Integer messageId) {
        Optional<String> token = CommandUtil.extractFirstParameter(getCommand(), callbackQuery.getData());
        token.ifPresent(string -> userSessionService.setHistoryItemMessageId(callbackQuery.getFrom().getId(), string, messageId));
    }

    protected void handleMissingHistoryItem(User user) {
        telegramService.sendErrorMessage(user.getId());
    }
}
