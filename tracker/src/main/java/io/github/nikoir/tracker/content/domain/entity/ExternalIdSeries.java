package io.github.nikoir.tracker.content.domain.entity;

import io.github.nikoir.tracker.content.domain.entity.dictionary.DictExternalId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "external_id_series")
public class ExternalIdSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_id", nullable = false)
    private DictExternalId externalId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @NotNull
    @Column(name="value")
    private String value;
}
