package io.github.nikoir.seriesparser.adapter;

import io.github.nikoir.seriesparser.dto.response.SeriesViewRs;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.Image;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.KinopoiskExternalId;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.KinopoiskSeriesSearchRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static io.github.nikoir.seriesparser.enums.ExternalId.*;

@Mapper(componentModel = "spring")
public abstract class KinopoiskSeriesAdapter implements SeriesAdapter<KinopoiskSeriesSearchRs> {
    @Mapping(target="title", source = "name")
    @Mapping(target="posterUrl", source = "poster")
    @Mapping(target="externalIds", source = ".")
    @Mapping(target="totalSeasons", ignore = true)
    abstract SeriesViewRs toViewDto(KinopoiskSeriesSearchRs.Doc doc);

    protected String mapPosterToPosterUrl(Image poster) {
        return Optional.ofNullable(poster)
                .map(Image::previewUrl)
                .orElse(null);
    }

    @Override
    public PagedModel<SeriesViewRs> toViewDtoPage(KinopoiskSeriesSearchRs searchRs) {
        if (searchRs == null) {
            return new PagedModel<>(Page.empty());
        }

        List<SeriesViewRs> content = new ArrayList<>();
        if (!CollectionUtils.isEmpty(searchRs.docs())) {
            content = searchRs
                    .docs()
                    .stream()
                    .map(this::toViewDto)
                    .toList();
        }
        PageRequest pageRequest = PageRequest.of(searchRs.page(), searchRs.limit());

        PageImpl<SeriesViewRs> seriesPage = new PageImpl<>(content, pageRequest, searchRs.total());

        return new PagedModel<>(seriesPage);
    }

    protected Map<String, String> mapExternalIds(KinopoiskSeriesSearchRs.Doc doc) {
        if (doc == null || doc.externalId() == null) {
            return Collections.emptyMap();
        }

        Map<String, String> externalIds = new HashMap<>();

        if (doc.id() != null) {
            externalIds.put(KINOPOISK.getSourceName(), String.valueOf(doc.id()));
        }

        Optional.ofNullable(doc.externalId().get(KinopoiskExternalId.IMDB.getName()))
                .ifPresent(imdbId -> externalIds.put(IMDB.getSourceName(), imdbId));

        Optional.ofNullable(doc.externalId().get(KinopoiskExternalId.TMDB.getName()))
                .ifPresent(tmdbId -> externalIds.put(TMDB.getSourceName(), tmdbId));

        Optional.ofNullable(doc.externalId().get(KinopoiskExternalId.KINOPOISK_HD.getName()))
                .ifPresent(kpHDId -> externalIds.put(KINOPOISK_HD.getSourceName(), kpHDId));

        return Collections.unmodifiableMap(externalIds);
    }
}
