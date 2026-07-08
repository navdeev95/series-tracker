package io.github.nikoir.tracker.content.domain.entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "iso_code", length = 2, nullable = false)
    private String isoCode;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "eng_name", nullable = false)
    private String engName;

    @ManyToMany(mappedBy = "countries")
    @Builder.Default
    private Set<Series> series = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(isoCode, country.isoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isoCode);
    }
}