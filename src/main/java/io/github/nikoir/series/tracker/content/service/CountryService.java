package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.common.dto.response.CountryRs;
import io.github.nikoir.series.tracker.content.domain.repo.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<CountryRs> findByIsoCodeIn(List<String> isoCodes) {
        if (isoCodes == null || isoCodes.isEmpty()) {
            return Collections.emptyList();
        }

        return countryRepository
                .findByIsoCodeIn(isoCodes)
                .stream()
                .map(c -> new CountryRs(c.getIsoCode(), c.getName()))
                .toList();
    }
}
