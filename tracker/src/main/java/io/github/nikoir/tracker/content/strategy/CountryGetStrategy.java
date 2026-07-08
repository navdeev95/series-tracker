package io.github.nikoir.tracker.content.strategy;

import io.github.nikoir.common.dto.response.CountryRs;

import java.util.List;

public interface CountryGetStrategy {
    List<CountryRs> getCountriesByCodes(List<String> isoCodes);
}
