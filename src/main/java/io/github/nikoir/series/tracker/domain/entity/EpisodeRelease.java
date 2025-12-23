package io.github.nikoir.series.tracker.domain.entity;

import io.github.nikoir.series.tracker.domain.entity.dictionary.DictDubStudio;
import io.github.nikoir.series.tracker.domain.entity.dictionary.DictQuality;
import io.github.nikoir.series.tracker.domain.entity.dictionary.DictSource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "episode_release")
@EntityListeners(AuditingEntityListener.class)
public class EpisodeRelease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dub_studio_id")
    private DictDubStudio dubStudio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private DictSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_id")
    private DictQuality quality;

    @CreatedDate
    @Column(name = "release_timestamp", nullable = false, updatable = false)
    private Instant releaseTimestamp;

}
