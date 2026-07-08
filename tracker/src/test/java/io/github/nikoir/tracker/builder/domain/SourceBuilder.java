package io.github.nikoir.tracker.builder.domain;

import io.github.nikoir.tracker.content.domain.entity.dictionary.DictSource;
import io.github.nikoir.tracker.content.enums.Source;

public class SourceBuilder {
    private final Source sourceTemplate;
    private String customName;
    private String customUrlTemplate;

    public SourceBuilder(Source source) {
        this.sourceTemplate = source;
    }

    public SourceBuilder withName(String name) {
        this.customName = name;
        return this;
    }

    public SourceBuilder withUrlTemplate(String urlTemplate) {
        this.customUrlTemplate = urlTemplate;
        return this;
    }

    public DictSource build() {
        return DictSource.builder()
                .id(sourceTemplate.getEntityId())
                .name(customName != null ? customName : sourceTemplate.getName())
                .rootUrl(sourceTemplate.getRootUrl())
                .urlTemplate(customUrlTemplate != null? customUrlTemplate : sourceTemplate.getUrlTemplate())
                .build();
    }
}
