package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.entity.User;
import io.github.nikoir.series.tracker.domain.entity.UserSubscription;
import io.github.nikoir.series.tracker.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.domain.repo.UserRepository;
import io.github.nikoir.series.tracker.domain.repo.UserSubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SeriesRepository seriesRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    public boolean isUserSubscribed(Long userTelegramId, Long seriesId) {
        return userSubscriptionRepository.existsByTelegramIdAndSeriesId(userTelegramId, seriesId);
    }

    @Transactional
    public void subscribe(Long userTelegramId, Long seriesId) {
        User user = userService.getOrCreate(userTelegramId);
        if (!seriesRepository.existsById(seriesId)) {
            //TODO: кастомные исключения
            throw new IllegalArgumentException(String.format("Not found series by id %s", seriesId));
        }
        userSubscriptionRepository.save(UserSubscription.builder()
                        .user(user)
                        .series(seriesRepository.getReferenceById(seriesId))
                .build());
    }
}
