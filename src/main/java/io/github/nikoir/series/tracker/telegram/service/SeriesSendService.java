package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.dto.internal.SeriesDetailViewRs;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.telegram.model.session.SeriesHistoryItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static liquibase.util.StringUtil.escapeHtml;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesSendService {
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;

    public void sendSeriesListInline(String inlineQueryId,
                                     Long userId,
                                     PagedModel<SeriesShortViewRs> seriesList) {
        long page = seriesList.getMetadata().number();
        long totalPages = seriesList.getMetadata().totalPages();

        List<InlineQueryResult> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(seriesList.getContent())) {
            results.add(createNoResultsArticle());
        } else {
            for (int i = 0; i < seriesList.getContent().size(); i++) {
                SeriesShortViewRs series = seriesList.getContent().get(i);
                String token = addHistoryItem(userId, series);
                results.add(createSeriesResult(series, token, i));
            }
        }

        AnswerInlineQuery answer = AnswerInlineQuery.builder()
                .inlineQueryId(inlineQueryId)
                .cacheTime(1)
                .isPersonal(true)
                .results(results)
                .nextOffset(page < totalPages - 1 ? String.valueOf(page + 1) : "")
                .build();

        try {
            telegramService.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error while sending inline-answer", e);
        }
    }

    public void sendSeriesDetailMessage(Long chatId, SeriesDetailViewRs seriesDetail) {
        SendPhoto answer = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(seriesDetail.posterUrl()))
                .caption(buildSeriesCaption(seriesDetail))
                .parseMode("HTML")
                .build();
        try {
            telegramService.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error while sending inline-answer", e);
        }

    }

    private String buildSeriesCaption(SeriesDetailViewRs seriesDetail) {
        return String.format("""
            <b>%s</b> (%d)
            
            <i>%s</i>
           
            🌏 <b>Страны:</b> %s
            📅 <b>Год:</b> %d
            📺 <b>Сезонов:</b> %d
            
            <b>Описание:</b>
            %s
            
            """,
                seriesDetail.title(),
                seriesDetail.releaseYear(),
                seriesDetail.isSeries()? "Сериал": "Фильм",
                String.join(", ", seriesDetail.countries()),
                seriesDetail.releaseYear(),
                seriesDetail.totalSeasons(),
                seriesDetail.description());
    }


    private InlineQueryResultArticle createSeriesResult(SeriesShortViewRs series, String token, int index) {
        InputTextMessageContent messageContent = InputTextMessageContent.builder()
                .messageText(String.format("%s (%d)\n%s",
                        series.title(),
                        series.year(),
                        series.isSeries() ? "Сериал": "Фильм"))
                .parseMode("Markdown")
                .build();

        return InlineQueryResultArticle
                .builder()
                .id(String.valueOf(index))
                .title(series.title())
                .description(String.format("%d %s", series.year(), series.isSeries() ? "Сериал": "Фильм"))
                .inputMessageContent(messageContent)
                .replyMarkup(createPreviewKeyboard(token))
                .thumbnailUrl(series.posterUrl())
                .build();
    }

    private String addHistoryItem(Long userId, SeriesShortViewRs series) {
        String token = UUID.randomUUID().toString();
        SeriesHistoryItem historyItem = new SeriesHistoryItem(token, series.externalIds());
        userSessionService.addHistoryItem(userId, historyItem);
        return token;
    }

    private InlineQueryResultArticle createNoResultsArticle() {
        return InlineQueryResultArticle.builder()
                .id("no_results")
                .title("Ничего не найдено 😔")
                .description("Попробуйте изменить запрос")
                .build();
    }

    private InlineKeyboardMarkup createPreviewKeyboard(String token) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("ℹ️ Подробнее")
                        .callbackData("series_detail:" + token)
                        .build()))
                .build();
    }
}
