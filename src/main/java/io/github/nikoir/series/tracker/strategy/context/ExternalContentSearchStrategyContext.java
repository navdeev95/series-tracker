package io.github.nikoir.series.tracker.strategy.context;

import io.github.nikoir.series.tracker.dto.internal.SeasonViewRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import io.github.nikoir.series.tracker.strategy.impl.MovieLabExternalContentSearchStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Getter
@RequiredArgsConstructor
public class ExternalContentSearchStrategyContext {
    private final MovieLabExternalContentSearchStrategy externalContentSearchStrategy;

     public List<SeasonViewRs> search(Map<ExternalId, String> externalIds) {
         String kinopoiskId = externalIds.get(ExternalId.KINOPOISK);
         if (kinopoiskId == null) {
             throw new IllegalArgumentException("Not found kinopoiskId");
         }
         return externalContentSearchStrategy.search(kinopoiskId);
     }
}
