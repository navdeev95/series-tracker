package io.github.nikoir.series.tracker.telegram.model.session;

import io.github.nikoir.series.tracker.telegram.model.session.context.SearchContext;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    private Long userId;
    private UserStateEnum userState;
    private LocalDateTime lastActivity;

    private SearchContext searchContext;

    public UserSession(UserStateEnum state) {
        this.userState = state;
        this.lastActivity = LocalDateTime.now();
    }

    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    public void resetContext() {
        this.searchContext = null;
    }
}
