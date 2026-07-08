package io.github.nikoir.tracker.telegram.model.session;

import io.github.nikoir.tracker.content.enums.Source;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchContext {
    Source searchSource;
}
