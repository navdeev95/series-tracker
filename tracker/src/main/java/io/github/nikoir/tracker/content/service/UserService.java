package io.github.nikoir.tracker.content.service;

import io.github.nikoir.tracker.content.domain.entity.User;
import io.github.nikoir.tracker.content.domain.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User getOrCreate(Long telegramId) {
        Optional<User> user = userRepository.findByTelegramId(telegramId);
        if (user.isPresent()) {
            return user.get();
        }
        User createdUser = userRepository.save(User
                .builder()
                .telegramId(telegramId)
                .build());
        log.debug("Successfully created user with id {}", createdUser.getId());

        return createdUser;
    }
}
