package io.github.nikoir.seriesparser.service;

import io.github.nikoir.seriesparser.domain.repo.SeriesRepository;
import io.github.nikoir.seriesparser.dto.response.SeriesViewDto;
import io.github.nikoir.seriesparser.mapper.SeriesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DbSeriesSearchService {
    private final SeriesRepository seriesRepository;
    private final SeriesMapper seriesMapper;

    public List<SeriesViewDto> findSeriesByTitle(String title) {
        return seriesMapper
                .toViewDtoList(seriesRepository.findByTitle(title));
    }
}
