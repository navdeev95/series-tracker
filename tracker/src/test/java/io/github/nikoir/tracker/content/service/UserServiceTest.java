package io.github.nikoir.tracker.content.service;

import io.github.nikoir.tracker.builder.domain.UserBuilder;
import io.github.nikoir.tracker.content.domain.entity.User;
import io.github.nikoir.tracker.content.domain.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserBuilder builder = new UserBuilder();
        this.user = builder.build();
    }

    @Test
    public void getOrCreate_UserExists_ShouldReturnExistingUser() {
        when(userRepository.findByTelegramId(user.getTelegramId())).thenReturn(Optional.of(user));
        User foundUser = userService.getOrCreate(user.getTelegramId());

        assertEquals(user.getTelegramId(), foundUser.getTelegramId());
    }

    @Test
    public void getOrCreate_UserNotExists_ShouldCreateNewUser() {
        when(userRepository.findByTelegramId(user.getTelegramId())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.getOrCreate(user.getTelegramId());

        assertEquals(user.getTelegramId(), createdUser.getTelegramId());
        verify(userRepository).save(any(User.class));
    }
}
