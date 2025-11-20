package io.github.nikoir.seriesparser.controller;

import io.github.nikoir.seriesparser.dto.request.SeriesSearchRq;
import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.service.series.content.create.KinopoiskContentCreateService;
import io.github.nikoir.seriesparser.service.series.search.MovieLabSearchStrategy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final KinopoiskContentCreateService kinopoiskContentCreateService;
    private final MovieLabSearchStrategy movieLabSearchStrategy;

    @PostMapping("/create-series")
    public void create(@RequestBody Map<String, String> externalIds) {
        kinopoiskContentCreateService.createSeries(externalIds);
    }

    @GetMapping("/search-series")
    public PagedModel<SeriesViewRs> search(@Valid SeriesSearchRq searchRq) {
        return movieLabSearchStrategy.search(searchRq);
    }
}
