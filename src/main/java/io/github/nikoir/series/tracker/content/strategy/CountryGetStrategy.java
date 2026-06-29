package io.github.nikoir.series.tracker.content.strategy;

import io.github.nikoir.series.tracker.common.dto.response.CountryRs;

import java.util.List;

public interface CountryGetStrategy {
    List<CountryRs> getCountriesByCodes(List<String> isoCodes);
}
