package io.github.nikoir.tracker.telegram.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.nikoir.common.dto.response.SeriesDetailViewRs;
import io.github.nikoir.tracker.telegram.model.session.SeriesHistoryItem;
import io.github.nikoir.tracker.telegram.model.session.UserSession;
import io.github.nikoir.tracker.telegram.model.session.UserStateEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Service
public class UserSessionService {
    private Cache<Long, UserSession> userSessionsCache;

    @PostConstruct
    public void init() {
        userSessionsCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .removalListener((key, value, cause) ->
                        log.debug("Removed user's session {}: {}", key, cause)
                )
                .recordStats()
                .build();
    }

    public void setUserState(Long userId, UserStateEnum userState) {
        updateSession(userId, session -> session.setUserState(userState));
        log.debug("State {} set for user {}", userState, userId);
    }

    public UserStateEnum getUserState(Long userId) {
        UserStateEnum state;
        UserSession session = userSessionsCache.getIfPresent(userId);

        if (session != null) {
            state = session.getUserState();
            session.updateActivity();
            userSessionsCache.put(userId, session);
            return state;
        }

        return UserStateEnum.DEFAULT;
    }

    public void clearUserState(Long userId) {
        userSessionsCache.invalidate(userId);
    }

    public UserSession getOrCreateSession(Long userId) {
        return userSessionsCache.get(userId, id -> {
            log.debug("Creating new session for user: {}", id);

            return UserSession.builder()
                    .userId(id)
                    .userState(UserStateEnum.MAIN_MENU)
                    .lastActivity(LocalDateTime.now())
                    .build();
        });
    }

    public void updateSession(Long userId, Consumer<UserSession> updater) {
        UserSession session = getOrCreateSession(userId);
        updater.accept(session);
        session.updateActivity();
    }

    public void addHistoryItem(Long userId, SeriesHistoryItem historyItem) {
        UserSession session = getOrCreateSession(userId);
        session.addToHistory(historyItem);
        log.debug("Set history item {} for user {}", historyItem, userId);
    }

    public Optional<SeriesHistoryItem> getHistoryItem(Long userId, String token) {
        UserSession session = getOrCreateSession(userId);
        return session.getHistoryItem(token);
    }

    public boolean setHistoryItemMessageId(Long userId, String token, Integer messageId) {
        UserSession session = getOrCreateSession(userId);
        return session.setHistoryItemMessageId(token, messageId);
    }

    public boolean setHistoryItemSeriesDetails(Long userId, String token, SeriesDetailViewRs seriesDetails) {
        UserSession session = getOrCreateSession(userId);
        return session.setHistoryItemSeriesDetails(token, seriesDetails);
    }
}
