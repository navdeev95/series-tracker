package io.github.nikoir.series.tracker.telegram.model.session.context;

import io.github.nikoir.series.tracker.enums.Source;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchContext {
    Source searchSource;
}
