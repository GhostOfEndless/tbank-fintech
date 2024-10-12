package com.example.service;

import com.example.aspect.LogExecutionTime;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Profile("prod")
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final ExecutorService dataLoaderThreadPool;
    private final ScheduledExecutorService scheduledDataInitPool;
    private final Duration dataInitSchedule;

    @EventListener(ContextRefreshedEvent.class)
    @LogExecutionTime
    public void onApplicationEvent() {
        log.debug("Submit scheduled task for data loading");
        scheduledDataInitPool.scheduleAtFixedRate(this::loadData,
                0, dataInitSchedule.toSeconds(), TimeUnit.SECONDS);
    }

    public void loadData() {
        var latch = new CountDownLatch(2);

        log.debug("Starting tasks for data loading");
        var startTime = System.currentTimeMillis();
        dataLoaderThreadPool.submit(() -> {
            categoryService.init();
            latch.countDown();
        });

        dataLoaderThreadPool.submit(() -> {
            locationService.init();
            latch.countDown();
        });

        try {
            latch.await();
            var finishTime = System.currentTimeMillis();
            var workingTime = finishTime - startTime;
            log.debug("Loading finished in {} ms", workingTime);
        } catch (InterruptedException e) {
            log.error("Error occurred while loading data!");
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void onApplicationShutdown() {
        dataLoaderThreadPool.shutdown();
        scheduledDataInitPool.shutdown();

        try {
            if (!dataLoaderThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                dataLoaderThreadPool.shutdownNow();
            }
            if (!scheduledDataInitPool.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduledDataInitPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
