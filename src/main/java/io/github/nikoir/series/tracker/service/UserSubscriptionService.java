package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.domain.entity.User;
import io.github.nikoir.series.tracker.domain.entity.UserSubscription;
import io.github.nikoir.series.tracker.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.domain.repo.UserRepository;
import io.github.nikoir.series.tracker.domain.repo.UserSubscriptionRepository;
import io.github.nikoir.series.tracker.dto.external.request.SeriesSubscriptionRq;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SeriesRepository seriesRepository;
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

    public void unsubscribe(Long userTelegramId, Long seriesId) {
        userSubscriptionRepository.deleteSubscription(userTelegramId, seriesId);
    }

    public Page<Series> getSubscriptionList(SeriesSubscriptionRq rq) {
        PageRequest request = PageRequest.of(rq.page(), rq.limit());
        return userSubscriptionRepository.getUserSubscriptions(rq.userTelegramId(), request);
    }
}
