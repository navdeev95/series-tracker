package io.github.nikoir.tracker.content.domain.repo;

import io.github.nikoir.tracker.content.domain.entity.UserSubscription;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    @Query("SELECT EXISTS (SELECT 1 FROM UserSubscription us " +
            "WHERE us.user.telegramId = :telegramId AND us.series.id = :seriesId)")
    boolean existsByTelegramIdAndSeriesId(@Param("telegramId") Long telegramId,
                                          @Param("seriesId") Long seriesId);
    @Modifying
    @Transactional
    @Query("DELETE FROM UserSubscription us " +
            "WHERE us.user.telegramId = :telegramId " +
            "AND us.series.id = :seriesId")
    void deleteSubscription(@Param("telegramId") Long telegramId,
                            @Param("seriesId") Long seriesId);

    @EntityGraph(attributePaths = {"series.externalIds.externalId"})
    @Query("SELECT us FROM UserSubscription us WHERE us.user.telegramId = :telegramId")
    Page<UserSubscription> getUserSubscriptions(@Param("telegramId") Long telegramId,
                                      Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT us FROM UserSubscription us WHERE us.series.id = :seriesId")
    Page<UserSubscription> getSeriesSubscribers(@Param("seriesId") Long seriesId,
                                    Pageable pageable);
}
