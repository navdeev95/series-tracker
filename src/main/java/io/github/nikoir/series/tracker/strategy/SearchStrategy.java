package io.github.nikoir.series.tracker.strategy;

import io.github.nikoir.series.tracker.enums.Source;

public interface SearchStrategy<T, R> {
    Source getDataSource();
    R search(T request);
}
