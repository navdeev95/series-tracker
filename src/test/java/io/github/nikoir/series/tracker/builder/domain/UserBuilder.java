package io.github.nikoir.series.tracker.builder.domain;

import io.github.nikoir.series.tracker.content.domain.entity.User;

public class UserBuilder {
    private long telegramId;

    public UserBuilder() {
        this.telegramId = 123456789L;
    }

    public UserBuilder withTelegramId(long telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    public User build() {
        return User.builder()
                .telegramId(telegramId)
                .build();
    }
}
