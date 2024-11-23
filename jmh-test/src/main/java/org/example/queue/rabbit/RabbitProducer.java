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

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void produceMessage() {
    rabbitTemplate.convertAndSend("topic", "message");
  }

  @Override
  public void stop() {
    rabbitTemplate.getConnectionFactory().resetConnection();
    rabbitTemplate.destroy();
    log.info("Rabbit producer stopped");
  }
}
