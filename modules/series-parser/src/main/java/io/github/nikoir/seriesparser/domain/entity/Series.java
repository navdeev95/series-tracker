package io.github.nikoir.seriesparser.domain.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.nikoir.seriesparser.enums.ExternalId.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "series")
public class Series {

    public enum Status {
        FILMING, PRE_PRODUCTION, COMPLETED, ANNOUNCED, POST_PRODUCTION
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

    @Type(JsonBinaryType.class)
    @Column(name = "external_ids", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> externalIds = new HashMap<>();

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Season> seasons = new ArrayList<>();
}