package io.github.nikoir.series.tracker.telegram.ui.factory;

import io.github.nikoir.series.tracker.common.dto.response.*;
import io.github.nikoir.series.tracker.common.events.NewContentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageFactory {
    private final KeyboardFactory keyboardFactory;

    public SendPhoto createSeriesDetailMessage(Long chatId,
                                               SeriesDetailPersonalizedRs seriesDetail,
                                               String seriesToken) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(seriesDetail.seriesInfo().getPosterUrl()))
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

    public SendMessage createSubscriptionsSendMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Для просмотра подписок нажмите на кнопку ниже")
                .parseMode("Markdown")
                .replyMarkup(keyboardFactory.createSubscriptionsKeyboard())
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

    public SendPhoto createNewEpisodeMessage(Long chatId,
                                             NewContentEvent newContentEvent) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(newContentEvent.getSeriesDetails().getPosterUrl()))
                .caption(buildNewEpisodeCaption(newContentEvent))
                .parseMode("HTML")
                .build();
    }

    private String buildSeriesDetailCaption(SeriesDetailViewRs seriesInfo) {
        String status = seriesInfo.getStatus() != null
                ? seriesInfo.getStatus().getDescription()
                : "Неизвестно";

        return String.format("""
            <b>%s</b> (%d)
            
            <i>%s</i>
           
            🌏 <b>Страны:</b> %s
            📅 <b>Год:</b> %d
            📺 <b>Сезонов:</b> %d
            📌 <b>Статус:</b> %s
            
            <b>Описание:</b>
            %s
            """,seriesInfo.getTitle(),
                seriesInfo.getReleaseYear(),
                seriesInfo.getIsSeries() ? "Сериал" : "Фильм",
                String.join(", ",
                        seriesInfo.getCountries()
                                .stream()
                                .map(CountryRs::name)
                                .toList()),
                seriesInfo.getReleaseYear(),
                seriesInfo.getTotalSeasons(),
                status,
                seriesInfo.getDescription());
    }

    private String buildNewEpisodeCaption(NewContentEvent newContentEvent) {
        List<EpisodeReleaseViewRs> episodeReleases = newContentEvent.getEpisodeReleases();
        SeriesDetailViewRs seriesDetails = newContentEvent.getSeriesDetails();
        if (episodeReleases.isEmpty()) {
            return "";
        }
        return String.format("""
                <b>%s</b> (%d)
                %d эпизод
                Ссылка для просмотра: %s
                """,
                seriesDetails.getTitle(),
                episodeReleases.getFirst().episodeNumber(),
                episodeReleases.getFirst().episodeUrl());
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