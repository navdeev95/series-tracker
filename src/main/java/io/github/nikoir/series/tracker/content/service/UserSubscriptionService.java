package io.github.nikoir.series.tracker.content.service;

import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.User;
import io.github.nikoir.series.tracker.content.domain.entity.UserSubscription;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.domain.repo.UserSubscriptionRepository;
import io.github.nikoir.series.tracker.common.dto.request.SeriesSubscriptionRq;
import io.github.nikoir.series.tracker.content.enums.ExternalId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SeriesRepository seriesRepository;
    private final UserService userService;
    private final SeriesService seriesService;

    public boolean isUserSubscribed(Long userTelegramId, Long seriesId) {
        return userSubscriptionRepository.existsByTelegramIdAndSeriesId(userTelegramId, seriesId);
    }


    @Transactional
    public void unsubscribeByExternalIds(Long userTelegramId, Map<ExternalId, String> externalIds) {
        seriesService.find(externalIds)
                .map(Series::getId)
                .ifPresent(seriesId -> unsubscribe(userTelegramId, seriesId));
    }

    @Transactional
    public void subscribeIfNotExists(Long userTelegramId, Long seriesId) {
        if (!isUserSubscribed(userTelegramId, seriesId)) {
            subscribe(userTelegramId, seriesId);
        }
    }

    private void subscribe(Long userTelegramId, Long seriesId) {
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

    private void unsubscribe(Long userTelegramId, Long seriesId) {
        userSubscriptionRepository.deleteSubscription(userTelegramId, seriesId);
    }

    public Page<Series> getSubscriptionList(SeriesSubscriptionRq rq) {
        PageRequest request = PageRequest.of(rq.page(), rq.limit());
        return userSubscriptionRepository.getUserSubscriptions(rq.userTelegramId(), request);
    }
}
