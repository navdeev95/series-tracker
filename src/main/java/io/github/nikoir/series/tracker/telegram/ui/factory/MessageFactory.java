package io.github.nikoir.series.tracker.telegram.ui.factory;

import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.dto.external.response.SeriesListViewRs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;

@Component
@RequiredArgsConstructor
public class MessageFactory {
    private final KeyboardFactory keyboardFactory;

    public SendPhoto createSeriesDetailMessage(Long chatId,
                                               SeriesDetailPersonalizedRs seriesDetail,
                                               String seriesToken) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(seriesDetail.seriesInfo().posterUrl()))
                .caption(buildSeriesDetailCaption(seriesDetail.seriesInfo()))
                .parseMode("HTML")
                .replyMarkup(keyboardFactory.createSeriesKeyboard(seriesDetail, seriesToken))
                .build();
    }

    public InputTextMessageContent createSeriesPreviewContent(SeriesListViewRs series) {
        return InputTextMessageContent.builder()
                .messageText(String.format("%s (%d)\n%s",
                        series.title(),
                        series.year(),
                        series.isSeries() ? "Сериал" : "Фильм"))
                .parseMode("Markdown")
                .build();
    }

    public SendMessage createSearchSendMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Для поиска сериалов нажмите кнопку ниже")
                .parseMode("Markdown")
                .replyMarkup(keyboardFactory.createSearchKeyboard())
                .build();
    }

    public SendMessage createWelcomeMessage(User user) {
        return SendMessage
                .builder()
                .chatId(user.getId().toString())
                .text(getWelcomeMessage(user.getFirstName()))
                .replyMarkup(keyboardFactory.createMainKeyboard())
                .build();
    }

    private String buildSeriesDetailCaption(SeriesDetailViewRs seriesInfo) {
        return String.format("""
            <b>%s</b> (%d)
            
            <i>%s</i>
           
            🌏 <b>Страны:</b> %s
            📅 <b>Год:</b> %d
            📺 <b>Сезонов:</b> %d
            
            <b>Описание:</b>
            %s
            """,
                seriesInfo.title(),
                seriesInfo.releaseYear(),
                seriesInfo.isSeries() ? "Сериал" : "Фильм",
                String.join(", ", seriesInfo.countries()),
                seriesInfo.releaseYear(),
                seriesInfo.totalSeasons(),
                seriesInfo.description());
    }

    private String getWelcomeMessage(String userName) {
        return String.format("""
            🎬 Привет, %s! Добро пожаловать!
            
            Я буду уведомлять тебя о выходе новых серий любимых сериалов.
            
            📋 Что я умею:
            • Уведомлять о новых сериях
            • Помогать искать сериалы
            • Управлять подписками
            
            🚀 Начни с команды /search чтобы найти первый сериал!
            """, userName);
    }
}