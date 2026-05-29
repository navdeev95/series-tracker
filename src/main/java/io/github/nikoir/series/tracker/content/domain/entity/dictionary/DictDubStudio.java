package io.github.nikoir.series.tracker.content.domain.entity.dictionary;

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
@Table(name = "dub_studio")
public class DictDubStudio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Type(io.hypersistence.utils.hibernate.type.array.ListArrayType.class)
    @Column(
            name = "aliases",
            columnDefinition = "varchar[]"
    )
    private List<String> aliases;
}
