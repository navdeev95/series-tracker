package io.github.nikoir.series.tracker.content.domain;

import io.github.nikoir.series.tracker.builder.domain.SeriesBuilder;
import io.github.nikoir.series.tracker.builder.domain.UserBuilder;
import io.github.nikoir.series.tracker.builder.domain.UserSubscriptionBuilder;
import io.github.nikoir.series.tracker.content.domain.entity.Series;
import io.github.nikoir.series.tracker.content.domain.entity.User;
import io.github.nikoir.series.tracker.content.domain.entity.UserSubscription;
import io.github.nikoir.series.tracker.content.domain.repo.SeriesRepository;
import io.github.nikoir.series.tracker.content.domain.repo.UserRepository;
import io.github.nikoir.series.tracker.content.domain.repo.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserSubscriptionRepositoryTest {
    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private UserRepository userRepository;

    private long userTelegramId;
    private long seriesId;

    @BeforeEach
    void setUp() {
        User user = new UserBuilder().build();
        Series series = new SeriesBuilder().build();

        user = userRepository.save(user);
        series = seriesRepository.save(series);

        this.userTelegramId = user.getTelegramId();
        this.seriesId = series.getId();

        UserSubscription userSubscription = new UserSubscriptionBuilder()
                .withUser(user)
                .withSeries(series)
                .build();

        userSubscriptionRepository.save(userSubscription);
    }

    @Test
    public void existsByTelegramIdAndSeriesId_existingSubscription_ShouldReturnTrue() {
        assertTrue(userSubscriptionRepository.existsByTelegramIdAndSeriesId(userTelegramId, seriesId));
    }

    @Test
    public void existsByTelegramIdAndSeriesId_unexistingSubscription_ShouldReturnFalse() {
        assertFalse(userSubscriptionRepository.existsByTelegramIdAndSeriesId(userTelegramId, seriesId + 1));
    }

    @Test
    public void deleteSubscription_existingSubscription_ShouldDelete() {
        userSubscriptionRepository.deleteSubscription(userTelegramId, seriesId);

        Page<UserSubscription> subscriptions = userSubscriptionRepository.getUserSubscriptions(userTelegramId, Pageable.ofSize(10));
        assertTrue(subscriptions.isEmpty());
    }

    @Test
    public void deleteSubscription_unexistingSubscription_ShouldNotDelete() {
        userSubscriptionRepository.deleteSubscription(userTelegramId, seriesId + 1);

        Page<UserSubscription> series = userSubscriptionRepository.getUserSubscriptions(userTelegramId, Pageable.ofSize(10));
        assertTrue(series.stream().anyMatch(s -> Objects.equals(s.getSeries().getId(), seriesId)));
    }

    @Test
    public void getSeriesSubscribers_existingSubscription_ShouldReturnSubscriber() {
        Page<UserSubscription> subscribers = userSubscriptionRepository
                .getSeriesSubscribers(seriesId, Pageable.ofSize(10));

        assertEquals(1, subscribers.getContent().size());

        User user = subscribers.getContent().getFirst().getUser();
        assertEquals(userTelegramId, user.getTelegramId());
    }


    @Test
    public void getSeriesSubscribers_unexistingSubscription_ShouldNotReturnSubscriber() {
        Page<UserSubscription> subscribers = userSubscriptionRepository
                .getSeriesSubscribers(seriesId + 1, Pageable.ofSize(10));

        assertTrue(subscribers.getContent().isEmpty());
    }

}
