package org.example.queue;

import java.util.List;

public record QueueRunner(
    List<Producer> producerList,
    List<Consumer> consumerList
) {

  public void run() {
    producerList.forEach(Producer::produceMessage);
    consumerList.forEach(Consumer::consumeMessage);
  }

  public void stop() {
    producerList.forEach(Producer::stop);
    consumerList.forEach(Consumer::stop);
  }
}
