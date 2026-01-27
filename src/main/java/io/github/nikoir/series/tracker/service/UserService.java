package io.github.nikoir.series.tracker.service;

import io.github.nikoir.series.tracker.domain.entity.User;
import io.github.nikoir.series.tracker.domain.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public void createIfNotExists(Long telegramId) {
        if (!userRepository.existsByTelegramId(telegramId)) {
            User createdUser = userRepository.save(User
                    .builder()
                    .telegramId(telegramId)
                    .build());
            log.debug("Successfully created user with id {}", createdUser.getId());
        }
    }
}
