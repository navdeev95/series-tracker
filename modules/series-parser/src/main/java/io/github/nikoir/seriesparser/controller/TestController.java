package io.github.nikoir.seriesparser.controller;

import io.github.nikoir.seriesparser.dto.api.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.internal.SeriesShortViewRs;
import io.github.nikoir.seriesparser.service.create.content.SeriesCreateFacade;
import io.github.nikoir.seriesparser.service.search.series.MovieLabSearchStrategy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final SeriesCreateFacade seriesCreateFacade;
    private final MovieLabSearchStrategy movieLabSearchStrategy;

    @PostMapping("/create-series")
    public void create(@RequestBody Map<String, String> externalIds) {
        seriesCreateFacade.create(externalIds);
    }

    @GetMapping("/search-series")
    public PagedModel<SeriesShortViewRs> search(@Valid SeriesSearchRq searchRq) {
        return movieLabSearchStrategy.search(searchRq);
    }
}
