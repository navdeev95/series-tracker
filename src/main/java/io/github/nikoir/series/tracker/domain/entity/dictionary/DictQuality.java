package io.github.nikoir.series.tracker.domain.entity.dictionary;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "quality")
public class DictQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Type(io.hypersistence.utils.hibernate.type.array.ListArrayType.class)
    @Column(
            name = "aliases",
            columnDefinition = "varchar[]"
    )
    private List<String> aliases;

    @Column(name = "resolution_width", nullable = false)
    private Integer resolutionWidth;

    @Column(name = "resolution_height", nullable = false)
    private Integer resolutionHeight;
}
