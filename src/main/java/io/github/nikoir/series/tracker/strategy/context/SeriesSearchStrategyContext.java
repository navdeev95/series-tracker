package io.github.nikoir.series.tracker.strategy.context;

import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.strategy.SeriesSearchStrategy;
import io.github.nikoir.series.tracker.strategy.impl.DBSearchStrategy;
import io.github.nikoir.series.tracker.strategy.impl.KinopoiskSearchStrategy;
import io.github.nikoir.series.tracker.strategy.impl.MovieLabSearchStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SeriesSearchStrategyContext {
    private final KinopoiskSearchStrategy kinopoiskSearchStrategy;
    private final MovieLabSearchStrategy movieLabSearchStrategy;
    private final DBSearchStrategy dbSearchStrategy;

    private final List<SeriesSearchStrategy> searchChain = new LinkedList<>();

    @PostConstruct
    private void buildChain() {
        searchChain.add(kinopoiskSearchStrategy);
        searchChain.add(movieLabSearchStrategy);
        searchChain.add(dbSearchStrategy);
    }

    public PagedModel<SeriesShortViewRs> search(SeriesSearchRq request) {
        PagedModel<SeriesShortViewRs> result;
        for (SeriesSearchStrategy searchStrategy: searchChain) {
            try {
                result = searchStrategy.search(request);
                if (!CollectionUtils.isEmpty(result.getContent())) {
                    return result;
                }
            } catch(Exception ex) {
                log.error("Failed search for strategy: {}", searchStrategy.getClass(), ex);
            }

        }
        return createEmptyResult(request);
    }

    private PagedModel<SeriesShortViewRs> createEmptyResult(SeriesSearchRq request) {
        Page<SeriesShortViewRs> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(request.page(), request.limit()),
                0
        );
        return new PagedModel<>(emptyPage);
    }
}
