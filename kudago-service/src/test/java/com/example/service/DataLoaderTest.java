package com.example.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DataLoaderTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private EventService eventService;

    @Mock
    private LocationService locationService;

    private ExecutorService dataLoaderThreadPool;
    private ScheduledExecutorService scheduledDataInitPool;
    private DataLoader dataLoader;
    private Duration dataInitSchedule;

    @BeforeEach
    void setUp() {
        dataLoaderThreadPool = Executors.newFixedThreadPool(2);
        scheduledDataInitPool = Executors.newScheduledThreadPool(1);
        dataInitSchedule = Duration.ofSeconds(60);

        dataLoader = new DataLoader(
                categoryService,
                locationService,
                eventService,
                dataLoaderThreadPool,
                scheduledDataInitPool,
                dataInitSchedule
        );
    }

    @Test
    void loadData_ShouldInitializeBothServices() {
        dataLoader.loadData();

        verify(categoryService, timeout(1000)).init();
        verify(locationService, timeout(1000)).init();
    }

    @SneakyThrows
    @Test
    void onApplicationShutdown_ShouldShutdownExecutors() {
        dataLoader.onApplicationShutdown();

        assertThat(dataLoaderThreadPool.isShutdown()).isTrue();
        assertThat(scheduledDataInitPool.isShutdown()).isTrue();

        assertThat(dataLoaderThreadPool.awaitTermination(100, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(scheduledDataInitPool.awaitTermination(100, TimeUnit.MILLISECONDS)).isTrue();
    }

    @Test
    void loadData_ShouldHandleInterruptedException() {
        ExecutorService interruptibleExecutor = mock(ExecutorService.class);
        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(interruptibleExecutor).submit(any(Runnable.class));

        DataLoader dataLoader = new DataLoader(
                categoryService,
                locationService,
                eventService,
                interruptibleExecutor,
                scheduledDataInitPool,
                dataInitSchedule
        );

        dataLoader.loadData();

        assertThat(Thread.interrupted()).isTrue();
    }

    @AfterEach
    void tearDown() {
        dataLoaderThreadPool.shutdownNow();
        scheduledDataInitPool.shutdownNow();
    }
}
