package io.github.nikoir.series.tracker.telegram.ui.factory;

import io.github.nikoir.series.tracker.common.dto.response.CountryRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailPersonalizedRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
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
                .photo(new InputFile(seriesDetail.seriesInfo().getPosterUrl()))
                .caption(buildSeriesDetailCaption(seriesDetail.seriesInfo(), false, false))
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

    public SendPhoto createNewSeasonMessage(Long chatId,
                                            SeriesDetailViewRs seriesDetail) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(seriesDetail.getPosterUrl()))
                .caption(buildSeriesDetailCaption(seriesDetail, false, true))
                .parseMode("HTML")
                .build();
    }

    public SendPhoto createNewEpisodeMessage(Long chatId,
                                             SeriesDetailViewRs seriesDetail) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(seriesDetail.getPosterUrl()))
                .caption(buildSeriesDetailCaption(seriesDetail, true, false))
                .parseMode("HTML")
                .build();
    }

    private String buildSeriesDetailCaption(SeriesDetailViewRs seriesInfo,
                                            boolean isNewEpisodeEvent,
                                            boolean isNewSeasonEvent) {
        return String.format("""
            %s
            <b>%s</b> (%d)
            
            <i>%s</i>
           
            🌏 <b>Страны:</b> %s
            📅 <b>Год:</b> %d
            📺 <b>Сезонов:</b> %d
            
            <b>Описание:</b>
            %s
            """,
                buildTypeInfo(isNewEpisodeEvent, isNewSeasonEvent),
                seriesInfo.getTitle(),
                seriesInfo.getReleaseYear(),
                seriesInfo.getIsSeries() ? "Сериал" : "Фильм",
                String.join(", ",
                        seriesInfo.getCountries()
                                .stream()
                                .map(CountryRs::name)
                                .toList()),

                seriesInfo.getReleaseYear(),
                seriesInfo.getTotalSeasons(),
                seriesInfo.getDescription());
    }

    private String buildTypeInfo(boolean isNewEpisodeEvent, boolean isNewSeasonEvent) {
        if (isNewEpisodeEvent) {
            return "🎬 <i>Новый эпизод</i>";
        }
        if (isNewSeasonEvent) {
            return "📺 <i>Новый сезон</i>";
        }

        return "";
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