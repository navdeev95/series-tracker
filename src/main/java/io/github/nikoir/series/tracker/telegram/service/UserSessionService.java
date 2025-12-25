package io.github.nikoir.series.tracker.telegram.service;

import io.github.nikoir.series.tracker.telegram.model.UserStateEnum;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {
    private final ConcurrentHashMap<Long, UserStateEnum> userStates = new ConcurrentHashMap<>();

    public void setUserState(Long userId, UserStateEnum userState) {
        userStates.put(userId, userState);
    }

    public UserStateEnum getUserState(Long userId) {
        return userStates.get(userId);
    }

    public void clearUserState(Long userId) {
        userStates.remove(userId);
    }
}
