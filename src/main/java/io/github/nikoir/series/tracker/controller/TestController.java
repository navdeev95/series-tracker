package io.github.nikoir.series.tracker.controller;

import io.github.nikoir.series.tracker.dto.api.request.SeriesSearchRq;
import io.github.nikoir.series.tracker.dto.internal.SeriesShortViewRs;
import io.github.nikoir.series.tracker.service.create.content.SeriesCreateFacade;
import io.github.nikoir.series.tracker.service.search.series.MovieLabSearchStrategy;
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
