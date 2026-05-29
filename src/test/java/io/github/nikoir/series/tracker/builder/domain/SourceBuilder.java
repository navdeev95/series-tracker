package io.github.nikoir.series.tracker.builder.domain;

import io.github.nikoir.series.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.series.tracker.content.enums.Source;

public class SourceBuilder {
    private final Source sourceTemplate;
    private String customName;

    public SourceBuilder(Source source) {
        this.sourceTemplate = source;
    }

    public SourceBuilder withName(String name) {
        this.customName = name;
        return this;
    }

    public DictSource build() {
        return DictSource.builder()
                .id(sourceTemplate.getEntityId())
                .name(customName != null ? customName : sourceTemplate.getName())
                .rootUrl(sourceTemplate.getRootUrl())
                .urlTemplate(sourceTemplate.getUrlTemplate())
                .build();
    }
}
