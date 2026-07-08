package io.github.nikoir.tracker.builder.domain;

import io.github.nikoir.tracker.content.domain.entity.Series;
import io.github.nikoir.tracker.content.domain.entity.User;
import io.github.nikoir.tracker.content.domain.entity.UserSubscription;

public class UserSubscriptionBuilder {
    private User user;
    private Series series;

    public UserSubscriptionBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public UserSubscriptionBuilder withSeries(Series series) {
        this.series = series;
        return this;
    }

    public UserSubscription build() {
        return UserSubscription.builder()
                .user(user)
                .series(series)
                .build();
    }
}
