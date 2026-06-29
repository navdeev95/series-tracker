package io.github.nikoir.series.tracker.telegram.command.handler.base;

import io.github.nikoir.series.tracker.telegram.command.enums.CommandEnum;
import io.github.nikoir.series.tracker.telegram.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseCommand<T extends CommandEnum, E> {
    protected final TelegramService telegramService;

    public abstract T getCommand();
    public void execute(Update update) {
        E request = extractRequest(update);
        try {
            doExecute(request);
        } catch (Exception ex) {
            handleError(request, ex);
            log.error("Произошла ошибка при обработке ответа", ex);
        }
    }

    protected abstract E extractRequest(Update update);

    protected abstract void doExecute(E request);

    protected Long extractChatId(E request) {
        return extractUser(request).getId();
    }

    protected abstract User extractUser(E request);

    protected void handleError(E request, Exception ex) {
        Long chatId = extractChatId(request);
        telegramService.sendErrorMessage(chatId);
    }

    protected void handleError(E request) {
        handleError(request, null);
    }

}
