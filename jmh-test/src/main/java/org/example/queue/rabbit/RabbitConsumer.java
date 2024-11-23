package org.example.queue.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.queue.Consumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitConsumer implements Consumer {

  private final RabbitTemplate rabbitTemplate;

  @Override
  @RabbitListener(queues = "queue")
  public void consumeMessage() {
    rabbitTemplate.receive("queue");
    log.info("Rabbit consumer message received");
  }

  @Override
  public void stop() {
    rabbitTemplate.getConnectionFactory().resetConnection();
    rabbitTemplate.destroy();
    log.info("Rabbit consumer stopped");
  }
}
