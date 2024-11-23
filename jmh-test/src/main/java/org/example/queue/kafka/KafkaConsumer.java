package org.example.queue.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.queue.Consumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer implements Consumer {

  @Override
  @KafkaListener(topics = "topic", groupId = "group_id")
  public void consumeMessage() {
    log.info("Kafka consumer message received");
  }

  @Override
  public void stop() {
    log.info("Kafka consumer stop");
  }
}
