package io.github.nikoir.seriesparser.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "series")
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "original_title", nullable = false)
    private String originalTitle;

    @Column(name = "total_seasons", nullable = false)
    private Integer totalSeasons;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "kinopoisk_id")
    private String kinopoiskId;

    @Column(name = "imdb_id")
    private String imdbId;
}
