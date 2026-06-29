package io.github.nikoir.series.tracker.content.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            // Останавливаем таймер и отправляем метрику с собранными тегами
            sample.stop(Timer.builder(metricName)
                    .description("Длительность и статус выполнения операций приложения")
                    .tag("status", status)
                    .tag("exception", exceptionClass)
                    .register(registry));
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
            // Останавливаем таймер и отправляем метрику с собранными тегами
            sample.stop(Timer.builder(metricName)
                    .description("Длительность и статус выполнения операций приложения")
                    .tag("status", status)
                    .tag("exception", exceptionClass)
                    .register(registry));
        }
        return result;
    }
}
