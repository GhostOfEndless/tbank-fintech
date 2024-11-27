package org.example.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.example.config.KafkaConfig;
import org.example.config.RabbitConfig;
import org.example.queue.kafka.KafkaConsumer;
import org.example.queue.kafka.KafkaProducer;
import org.example.queue.rabbit.RabbitConsumer;
import org.example.queue.rabbit.RabbitProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

public class QueueRunnersCreator {

  private ConfigurableApplicationContext kafkaContext;
  private ConfigurableApplicationContext rabbitContext;

  public QueueRunner createRunner(int producersCount, int consumersCount, QueueType queueType) {
    kafkaContext = new AnnotationConfigApplicationContext(KafkaConfig.class);
    rabbitContext = new AnnotationConfigApplicationContext(RabbitConfig.class);

    List<Producer> producers = new ArrayList<>();
    List<Consumer> consumers = new ArrayList<>();

    return switch (queueType) {
      case RABBITMQ -> createRabbitConfiguration(producersCount, consumersCount, consumers, producers);
      case KAFKA -> createKafkaConfiguration(producersCount, consumersCount, producers, consumers);
    };
  }

  private QueueRunner createKafkaConfiguration(
      int producersCount,
      int consumersCount,
      List<Producer> producers,
      List<Consumer> consumers
  ) {
    KafkaTemplate<String, String> kafkaTemplate = kafkaContext.getBean(KafkaTemplate.class);

    IntStream.range(0, consumersCount)
        .forEach(i -> consumers.add(new KafkaConsumer()));
    IntStream.range(0, producersCount)
        .forEach(i -> producers.add(new KafkaProducer(kafkaTemplate)));


    return new QueueRunner(producers, consumers);
  }

  private QueueRunner createRabbitConfiguration(
      int producersCount,
      int consumersCount,
      List<Consumer> consumers,
      List<Producer> producers
  ) {
    RabbitTemplate rabbitTemplate = rabbitContext.getBean(RabbitTemplate.class);

    IntStream.range(0, consumersCount)
        .forEach(i -> consumers.add(new RabbitConsumer(rabbitTemplate)));
    IntStream.range(0, producersCount)
        .forEach(i -> producers.add(new RabbitProducer(rabbitTemplate)));

    return new QueueRunner(producers, consumers);
  }

  public void clear() {
    if (rabbitContext != null) {
      rabbitContext.close();
      rabbitContext = null;
    }
    if (kafkaContext != null) {
      kafkaContext.close();
      kafkaContext = null;
    }
  }
}
