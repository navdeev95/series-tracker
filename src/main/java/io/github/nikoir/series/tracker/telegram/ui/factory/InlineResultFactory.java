package io.github.nikoir.series.tracker.telegram.ui.factory;

import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class InlineResultFactory {

    private final KeyboardFactory keyboardFactory;
    private final MessageFactory messageFactory;
    
    public PagedModel<InlineQueryResult> createSeriesResultInline(PagedModel<SeriesListViewRs> series, List<String> tokenList) {
        List<InlineQueryResult> inlineResults = IntStream.range(0, series.getContent().size())
                .mapToObj(i -> {
                    SeriesListViewRs s = series.getContent().get(i);
                    String token = tokenList.get(i);
                    return createSeriesResult(s, token, i);
                })
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of((int) series.getMetadata().number(), (int) series.getMetadata().size());
        return new PagedModel<>(new PageImpl<>(inlineResults, pageable, series.getMetadata().totalPages()));

    }

    private InlineQueryResultArticle createSeriesResult(SeriesListViewRs series, String token, int index) {
        return InlineQueryResultArticle.builder()
                .id(String.valueOf(index))
                .title(series.title())
                .description(String.format("%d %s", series.year(),
                        series.isSeries() ? "Сериал" : "Фильм"))
                .inputMessageContent(messageFactory.createSeriesPreviewContent(series))
                .replyMarkup(keyboardFactory.createPreviewKeyboard(token))
                .thumbnailUrl(series.posterUrl())
                .build();
    }

    public PagedModel<InlineQueryResult> createNoResultsInline() {
        Pageable pageable = PageRequest.of(0, 1);

        InlineQueryResult result = InlineQueryResultArticle.builder()
                .id("no_results")
                .title("Ничего не найдено 😔")
                .description("Попробуйте изменить запрос")
                .inputMessageContent(messageFactory.createNotFoundMessageContent())
                .build();

        return new PagedModel<>(new PageImpl<>(List.of(result), pageable, 0));
    }
}