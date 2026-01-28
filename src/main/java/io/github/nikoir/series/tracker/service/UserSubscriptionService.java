package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.repo.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;

    public boolean isUserSubscribed(Long userTelegramId, Long seriesId) {
        return userSubscriptionRepository.existsByTelegramIdAndSeriesId(userTelegramId, seriesId);
    }
}
