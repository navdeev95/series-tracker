package io.github.nikoir.series.tracker.content.domain.repo;

import io.github.nikoir.series.tracker.content.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramId(Long telegramId);
}
