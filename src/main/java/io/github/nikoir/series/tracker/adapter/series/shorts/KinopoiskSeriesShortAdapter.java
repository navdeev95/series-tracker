package io.github.nikoir.series.tracker.adapter.series.shorts;

import io.github.nikoir.series.tracker.adapter.series.KinopoiskExternalIdAdapter;
import io.github.nikoir.series.tracker.dto.external.response.SeriesListViewRs;
import io.github.nikoir.series.tracker.dto.integration.response.kinopoisk.Image;
import io.github.nikoir.series.tracker.dto.integration.response.kinopoisk.KinopoiskSeriesSearchRs;
import io.github.nikoir.series.tracker.enums.ExternalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Mapper(componentModel = "spring")
public abstract class KinopoiskSeriesShortAdapter implements SeriesShortAdapter<KinopoiskSeriesSearchRs> {
    @Autowired
    private KinopoiskExternalIdAdapter externalIdAdapter;

    @Mapping(target="title", source = ".", qualifiedByName = "getTitleWithPriority")
    @Mapping(target="posterUrl", source = "poster")
    @Mapping(target="externalIds", source = ".")
    @Mapping(target="totalSeasons", ignore = true)
    abstract SeriesListViewRs toViewDto(KinopoiskSeriesSearchRs.Doc doc);

    protected String mapPosterToPosterUrl(Image poster) {
        return Optional.ofNullable(poster)
                .map(Image::previewUrl)
                .orElse(null);
    }

    @Named("getTitleWithPriority")
    protected String getTitleWithPriority(KinopoiskSeriesSearchRs.Doc doc) {
        if (doc.name() != null && !doc.name().trim().isEmpty()) {
            return doc.name();
        }
        if (doc.alternativeName() != null && !doc.alternativeName().trim().isEmpty()) {
            return doc.alternativeName();
        }
        if (doc.enName() != null && !doc.enName().trim().isEmpty()) {
            return doc.enName();
        }
        return ""; // или null, или значение по умолчанию
    }

    @Override
    public PagedModel<SeriesListViewRs> toViewDtoPage(KinopoiskSeriesSearchRs searchRs) {
        if (searchRs == null) {
            return new PagedModel<>(Page.empty());
        }

        if (CollectionUtils.isEmpty(searchRs.docs())) {
            return SeriesShortAdapter.createEmptyPage();
        }
        List<SeriesListViewRs> content = searchRs
                .docs()
                .stream()
                .map(this::toViewDto)
                .toList();
        PageRequest pageRequest = PageRequest.of(searchRs.page() - 1, searchRs.limit());

        PageImpl<SeriesListViewRs> seriesPage = new PageImpl<>(content, pageRequest, searchRs.total());

        return new PagedModel<>(seriesPage);
    }

    protected Map<ExternalId, String> mapExternalIds(KinopoiskSeriesSearchRs.Doc doc) {
        return externalIdAdapter.mapExternalIds(String.valueOf(doc.id()), doc.externalId());
    }
}
