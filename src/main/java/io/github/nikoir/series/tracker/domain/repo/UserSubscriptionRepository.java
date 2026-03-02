package io.github.nikoir.series.tracker.domain.repo;

import io.github.nikoir.series.tracker.domain.entity.Series;
import io.github.nikoir.series.tracker.domain.entity.UserSubscription;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    @Query("SELECT COUNT(us) > 0 FROM UserSubscription us " +
            "JOIN us.user u " +
            "WHERE u.telegramId = :telegramId AND us.series.id = :seriesId")
    boolean existsByTelegramIdAndSeriesId(@Param("telegramId") Long telegramId,
                                          @Param("seriesId") Long seriesId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSubscription us " +
            "WHERE us.user.telegramId = :telegramId " +
            "AND us.series.id = :seriesId")
    void deleteSubscription(Long telegramId, Long seriesId);


    @Query("SELECT s FROM Series s " +
            "JOIN FETCH s.externalIds ei " +
            "WHERE s.id IN " +
            "(SELECT us.series.id FROM " +
                "UserSubscription us " +
                "JOIN us.user u " +
                "WHERE u.telegramId = :telegramId)")
    Page<Series> getUserSubscriptions(@Param("telegramId") Long telegramId, Pageable pageable);
}
