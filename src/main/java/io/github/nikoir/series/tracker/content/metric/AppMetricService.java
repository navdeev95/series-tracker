package io.github.nikoir.series.tracker.content.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class AppMetricService {
    private final MeterRegistry registry;

    public void record(String metricName, Runnable businessLogic) {
        Timer.Sample sample = Timer.start(registry);
        String status = "success";
        String exceptionClass = "none";

        try {
            businessLogic.run();
        } catch (Exception e) {
            status = "error";
            exceptionClass = e.getClass().getSimpleName();
            throw e;
        } finally {
            sampleStop(sample, metricName, status, exceptionClass);
        }
    }

    public <T> T recordWithResult(String metricName, Supplier<T> businessLogic) {
        Timer.Sample sample = Timer.start(registry);
        String status = "success";
        String exceptionClass = "none";
        T result;

        try {
            result = businessLogic.get();
        } catch (Exception e) {
            status = "error";
            exceptionClass = e.getClass().getSimpleName();
            throw e;
        } finally {
            sampleStop(sample, metricName, status, exceptionClass);
        }
        return result;
    }

    private void sampleStop(Timer.Sample sample,
                            String metricName,
                            String status,
                            String exceptionClass) {
        // Останавливаем таймер и отправляем метрику с собранными тегами
        sample.stop(Timer.builder(metricName)
                .tag("status", status)
                .tag("exception", exceptionClass)
                .sla(Duration.ofMillis(50),   // "Хорошо" — быстрее 50ms
                        Duration.ofMillis(100),  // "Приемлемо" — до 100ms
                        Duration.ofMillis(200),  // "Медленно" — до 200ms
                        Duration.ofMillis(500),  // "Критично" — до 500ms
                        Duration.ofMillis(1000)  // "Нарушение SLA" — >1s
                )
                .register(registry));
    }
}
