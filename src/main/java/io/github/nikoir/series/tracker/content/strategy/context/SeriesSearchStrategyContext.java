package io.github.nikoir.series.tracker.content.strategy.context;

import io.github.nikoir.series.tracker.common.dto.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.common.dto.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.common.dto.response.SeriesSearchRs;
import io.github.nikoir.series.tracker.content.enums.Source;
import io.github.nikoir.series.tracker.content.strategy.SeriesSearchStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.DBSearchStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.KinopoiskSearchStrategy;
import io.github.nikoir.series.tracker.content.strategy.impl.MovieLabSearchStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static io.github.nikoir.series.tracker.content.enums.Source.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class SeriesSearchStrategyContext {
    private final KinopoiskSearchStrategy kinopoiskSearchStrategy;
    private final MovieLabSearchStrategy movieLabSearchStrategy;
    private final DBSearchStrategy dbSearchStrategy;

    private final List<SeriesSearchStrategy> searchChain = new LinkedList<>();
    private final Map<Source, SeriesSearchStrategy> searchMap = new HashMap<>();

    @PostConstruct
    private void buildChain() {
        searchMap.put(KINOPOISK, kinopoiskSearchStrategy);
        searchMap.put(MOVIELAB, movieLabSearchStrategy);
        searchMap.put(DATABASE, dbSearchStrategy);

        searchChain.add(movieLabSearchStrategy);
        searchChain.add(kinopoiskSearchStrategy);
        searchChain.add(dbSearchStrategy);
    }

    public SeriesSearchRs search(SeriesSearchRq request) {
        logPageInfo(request.page());

        if (StringUtils.isEmpty(request.title())) {
            return createEmptyResult(request);
        }

        PagedModel<SeriesListViewRs> result;
        for (SeriesSearchStrategy searchStrategy: searchChain) {
            try {
                result = searchStrategy.search(request);
                if (!CollectionUtils.isEmpty(result.getContent())) {
                    return new SeriesSearchRs(result,
                            searchStrategy.getDataSource());
                }
            } catch(Exception ex) {
                log.error("Failed search for strategy: {}", searchStrategy.getClass(), ex);
            }
        }
        return createEmptyResult(request);

    }

    public SeriesSearchRs search(SeriesSearchRq request, Source previousSource) {
        logPageInfo(request.page());
        log.debug("prev source = {}", previousSource);

        if (StringUtils.isEmpty(request.title())) {
            return createEmptyResult(request);
        }

        log.debug("Trying to get prev source: {}", previousSource);
        SeriesSearchStrategy searchStrategy = searchMap.get(previousSource);
        log.debug("Get previousSource success");
        try {
            return new SeriesSearchRs(searchStrategy.search(request),
                    searchStrategy.getDataSource());

        } catch(Exception ex) {
            return createEmptyResult(request);
        }
    }

    private SeriesSearchRs createEmptyResult(SeriesSearchRq request) {
        Page<SeriesListViewRs> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(request.page(), request.limit()),
                0
        );
        return new SeriesSearchRs(new PagedModel<>(emptyPage), null);
    }

    private void logPageInfo(int page) {
        log.debug("page = {}", page);
    }
}
