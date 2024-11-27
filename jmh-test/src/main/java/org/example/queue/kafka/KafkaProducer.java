package org.example.queue.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.queue.Producer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer implements Producer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public void produceMessage() {
    kafkaTemplate.send("topic", "message");
  }

  @Override
  public void stop() {
    kafkaTemplate.getProducerFactory().getListeners().forEach(kafkaTemplate.getProducerFactory()::removeListener);
    kafkaTemplate.destroy();
    log.info("Kafka producer stopped");
  }
}
