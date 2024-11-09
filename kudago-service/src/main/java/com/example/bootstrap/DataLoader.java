package com.example.bootstrap;

import com.example.aspect.LogExecutionTime;
import com.example.bootstrap.command.impl.CategoryInitializationCommand;
import com.example.bootstrap.command.impl.LocationInitializationCommand;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Profile("prod")
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader {

  private final DataInitializationInvoker dataInitializationInvoker;
  private final CategoryInitializationCommand categoryCommand;
  private final LocationInitializationCommand locationCommand;
  private final ExecutorService dataLoaderThreadPool;
  private final ScheduledExecutorService scheduledDataInitPool;
  private final Duration dataInitSchedule;
  private final Duration dataInitTerminationTimeout;

  @PostConstruct
  public void setup() {
    dataInitializationInvoker.addCommand(categoryCommand);
    dataInitializationInvoker.addCommand(locationCommand);
  }

  @EventListener(ContextRefreshedEvent.class)
  @LogExecutionTime
  public void onApplicationEvent() {
    log.debug("Submit scheduled task for data loading");
    scheduledDataInitPool.scheduleAtFixedRate(
        this::loadData,
        0,
        dataInitSchedule.toSeconds(),
        TimeUnit.SECONDS
    );
  }

  public void loadData() {
    dataInitializationInvoker.executeCommands();
  }

  @PreDestroy
  public void onApplicationShutdown() {
    dataLoaderThreadPool.shutdown();
    scheduledDataInitPool.shutdown();

    try {
      if (!dataLoaderThreadPool.awaitTermination(dataInitTerminationTimeout.getSeconds(), TimeUnit.SECONDS)) {
        dataLoaderThreadPool.shutdownNow();
      }
      if (!scheduledDataInitPool.awaitTermination(dataInitTerminationTimeout.getSeconds(), TimeUnit.SECONDS)) {
        scheduledDataInitPool.shutdownNow();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
