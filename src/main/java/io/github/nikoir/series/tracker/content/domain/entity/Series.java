package io.github.nikoir.series.tracker.content.domain.entity;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "series")
public class Series {

    public enum Status {
        FILMING, PRE_PRODUCTION, COMPLETED, CONTINUING, ANNOUNCED, POST_PRODUCTION, DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "eng_title")
    private String engTitle;

    @PositiveOrZero
    @Column(name = "total_seasons")
    private Integer totalSeasons;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @NotNull
    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "description")
    private String description;

    //TODO: добавить отдельную таблицу-справочник для стран
    @Column(name = "countries")
    private List<String> countries;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExternalIdSeries> externalIds;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Season> seasons = new ArrayList<>();
}