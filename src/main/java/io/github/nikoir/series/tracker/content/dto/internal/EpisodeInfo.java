package io.github.nikoir.series.tracker.content.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeInfo {
    private String name;
    private Integer number;
    private Date releaseDate;
}
