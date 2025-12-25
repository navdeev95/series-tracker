package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaGroupService {
    private final SeriesNotificationBot bot;
    /**
     * Отправка карусели сериалов (до 10)
     */
    public void sendSeriesCarousel(Long chatId,
                                   List<SeriesShortViewRs> seriesList,
                                   String title) {
        if (seriesList.isEmpty()) {
            bot.sendHtmlMessage(chatId, "📭 Список пуст");
            return;
        }

        // Ограничиваем 10 сериалами (лимит Telegram)
        List<SeriesShortViewRs> limitedList = seriesList.stream()
                .limit(10)
                .toList();

        // Создаем медиагруппу
        List<InputMedia> mediaGroup = new ArrayList<>();

        for (int i = 0; i < limitedList.size(); i++) {
            SeriesShortViewRs series = limitedList.get(i);
            InputMediaPhoto media = createSeriesMedia(series, i);
            mediaGroup.add(media);
        }

        try {
            // Отправляем заголовок отдельно
            bot.sendHtmlMessage(chatId,
                    String.format("🎬 <b>%s</b> (%d результатов):", title, limitedList.size()));

            // Отправляем карусель
            bot.execute(new SendMediaGroup(chatId.toString(), mediaGroup));

        } catch (TelegramApiException e) {
            log.error("Ошибка отправки медиагруппы", e);
            sendSeriesListAsText(chatId, seriesList, title);
        }
    }

    private InputMediaPhoto createSeriesMedia(SeriesShortViewRs series, int index) {
        InputMediaPhoto media = new InputMediaPhoto();

        // Устанавливаем изображение
        media.setMedia(getSeriesPoster(series));

        // Создаем подпись для каждого фото
        String caption = getSeriesCaption(series, index);

        // Только для первого фото добавляем полную подпись
        if (index == 0) {
            caption += "\n<i>Листайте вправо →</i>";
        }

        media.setCaption(caption);
        media.setParseMode("HTML");

        return media;
    }

    private String getSeriesPoster(SeriesShortViewRs series) {
        //TODO: переделать
        if (StringUtils.isEmpty(series.posterUrl())) {
            return "https://upload.wikimedia.org/wikipedia/commons/a/a3/Image-not-found.png";
        }
        return series.posterUrl();
    }

    private void sendSeriesListAsText(Long chatId, List<SeriesShortViewRs> seriesList, String title) {
        // Fallback: отправляем как текст
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🎬 <b>%s</b> (%d):\n\n", title, seriesList.size()));

        for (int i = 0; i < seriesList.size(); i++) {
            SeriesShortViewRs series = seriesList.get(i);
            sb.append(getSeriesCaption(series, i));
        }

        bot.sendHtmlMessage(chatId, sb.toString());
    }

    private String getSeriesCaption(SeriesShortViewRs series, int index) {
        StringBuilder caption = new StringBuilder();

        // Номер и заголовок
        caption.append(index + 1)
                .append(". <b>")
                .append(series.title())
                .append("</b> (")
                .append(getTypeAndYear(series))
                .append(")\n");

        // Количество сезонов для сериалов
        if (series.isSeries() && series.totalSeasons() != null) {
            caption.append("Количество сезонов: ")
                    .append(series.totalSeasons())
                    .append("\n");
        }

        return caption.toString();
    }

    private String getTypeAndYear(SeriesShortViewRs series) {
        StringBuilder typeAndYear = new StringBuilder();

        typeAndYear.append(series.isSeries() ? "Сериал" : "Фильм");

        if (series.year() != null) {
            typeAndYear.append(" ").append(series.year()).append(" г.");
        }

        return typeAndYear.toString();
    }
}
