package io.github.nikoir.series.tracker.domain.repo;

import io.github.nikoir.series.tracker.domain.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    @Query("SELECT COUNT(us) > 0 FROM UserSubscription us " +
            "JOIN us.user u " +
            "WHERE u.telegramId = :telegramId AND us.series.id = :seriesId")
    boolean existsByTelegramIdAndSeriesId(@Param("telegramId") Long telegramId,
                                          @Param("seriesId") Long seriesId);
}
