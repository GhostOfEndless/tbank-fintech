package org.example.queue.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.queue.Producer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitProducer implements Producer {

  private final static String QUEUE_NAME = "queue";
  private final RabbitTemplate rabbitTemplate;

  @Override
  public void produceMessage(String topic, String message) {
    rabbitTemplate.convertAndSend(QUEUE_NAME, message);
  }

  @Override
  public void stop() {
    rabbitTemplate.getConnectionFactory().resetConnection();
    rabbitTemplate.destroy();
    log.info("Rabbit producer stopped");
  }
}
