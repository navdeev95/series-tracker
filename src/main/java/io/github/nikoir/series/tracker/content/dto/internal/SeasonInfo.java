package io.github.nikoir.series.tracker.content.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonInfo {
    private String name;
    private Integer number;
    private Date releaseDate;
    private List<EpisodeInfo> episodes = new LinkedList<>();
}
