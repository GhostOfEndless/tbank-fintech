package com.example.bootstrap;

import com.example.bootstrap.command.DataInitializationCommand;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializationInvoker {

  private final List<DataInitializationCommand> commands = new ArrayList<>();
  private final ExecutorService dataLoaderThreadPool;
  private final Duration dataInitTimeout;

  public void addCommand(DataInitializationCommand command) {
    commands.add(command);
  }

  public void executeCommands() {
    var startTime = System.currentTimeMillis();
    log.debug("Starting execution of {} initialization commands", commands.size());

    var latch = new CountDownLatch(commands.size());

    commands.forEach(command ->
        dataLoaderThreadPool.submit(() -> {
          try {
            command.execute();
          } catch (Exception e) {
            log.error("Error executing initialization command: {}", e.getMessage(), e);
          } finally {
            latch.countDown();
          }
        })
    );

    try {
      if (!latch.await(dataInitTimeout.toSeconds(), TimeUnit.SECONDS)) {
        log.warn("Timeout occurred while waiting for initialization commands to complete");
      }
      var executionTime = System.currentTimeMillis() - startTime;
      log.debug("All initialization commands completed in {} ms", executionTime);
    } catch (InterruptedException e) {
      log.error("Interrupted while waiting for initialization commands", e);
      Thread.currentThread().interrupt();
    }
  }
}
