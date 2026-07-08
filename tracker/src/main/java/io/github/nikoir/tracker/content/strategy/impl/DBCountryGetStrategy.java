package io.github.nikoir.tracker.content.strategy.impl;

import io.github.nikoir.common.dto.response.CountryRs;
import io.github.nikoir.tracker.content.service.CountryService;
import io.github.nikoir.tracker.content.strategy.CountryGetStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DBCountryGetStrategy implements CountryGetStrategy {
    private final CountryService countryService;
    @Override
    public List<CountryRs> getCountriesByCodes(List<String> isoCodes) {
        return countryService.findByIsoCodeIn(isoCodes);
    }
}
