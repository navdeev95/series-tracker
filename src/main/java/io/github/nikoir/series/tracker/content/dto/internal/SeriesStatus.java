package io.github.nikoir.series.tracker.content.dto.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeriesStatus {
    FILMING("Съемки"),
    PRE_PRODUCTION("Пред-продакшн"),
    COMPLETED("Завершен"),
    ANNOUNCED("Анонсирован"),
    POST_PRODUCTION("Пост-продакшн"),
    DELETED("Удален/Отменен"),
    CONTINUING("Продолжается");

    private final String description;
}
