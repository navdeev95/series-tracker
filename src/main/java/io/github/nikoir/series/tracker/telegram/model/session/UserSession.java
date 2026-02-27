package io.github.nikoir.series.tracker.telegram.model.session;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

@Getter
@Setter
@Builder
public class UserSession {
    private Long userId;
    private UserStateEnum userState;
    private LocalDateTime lastActivity;
    @Builder.Default
    private Deque<SeriesHistoryItem> queue = new ArrayDeque<>();
    private SearchContext searchContext;

    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    public void resetContext() {
        this.searchContext = null;
    }

    public void addToHistory(SeriesHistoryItem historyItem) {
        queue.addLast(historyItem);
        if (queue.size() > 50) {
            queue.removeFirst();
        }
    }

    public Optional<SeriesHistoryItem> getHistoryItem(String token) {
        return queue
                .stream()
                .filter(item -> StringUtils.equals(item.token, token))
                .findFirst();
    }

    public boolean setHistoryItemMessageId(String token, Integer messageId) {
        Optional<SeriesHistoryItem> historyItem = getHistoryItem(token);
        if (historyItem.isEmpty()) {
            return false;
        }
        historyItem.get().setMessageId(messageId);
        return true;
    }
}
