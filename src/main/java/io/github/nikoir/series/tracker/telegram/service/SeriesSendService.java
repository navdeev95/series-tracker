package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.telegram.bot.SeriesNotificationBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeriesSendService {
    private final SeriesNotificationBot bot;

    public void sendSeriesInline(String inlineQueryId,
                                 PagedModel<SeriesShortViewRs> seriesList) {
        long page = seriesList.getMetadata().number();
        long totalPages = seriesList.getMetadata().totalPages();

        List<InlineQueryResult> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(seriesList.getContent())) {
            results.add(createNoResultsArticle());
        } else {
            for (int i = 0; i < seriesList.getContent().size(); i++) {
                results.add(createSeriesResult(seriesList.getContent().get(i), i));
            }
        }

        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQueryId);
        answer.setResults(results);
        answer.setCacheTime(1); // Короткое кеширование
        answer.setIsPersonal(true);
        answer.setNextOffset(page < totalPages - 1 ? String.valueOf(page + 1) : "");

        try {
            bot.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке inline-ответа", e);
        }
    }

    private InlineQueryResultArticle createSeriesResult(SeriesShortViewRs series, int index) {
        InputTextMessageContent messageContent = new InputTextMessageContent();

        messageContent.setMessageText(String.format("%s (%d)\n%s",
                series.title(),
                series.year(),
                series.isSeries() ? "Сериал": "Фильм"));

        messageContent.setParseMode("Markdown");

        return InlineQueryResultArticle
                .builder()
                .id(String.valueOf(index))
                .title(series.title())
                .description(String.format("%d %s", series.year(), series.isSeries() ? "Сериал": "Фильм"))
                .inputMessageContent(messageContent)
                .thumbUrl(series.posterUrl())
                .build();
    }

    private InlineQueryResultArticle createNoResultsArticle() {
        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.setMessageText("Ничего не найдено.\n\nПопробуйте другой запрос.");

        return InlineQueryResultArticle.builder()
                .id("no_results")
                .title("Ничего не найдено 😔")
                .description("Попробуйте изменить запрос")
                .inputMessageContent(messageContent)
                .build();
    }
}
