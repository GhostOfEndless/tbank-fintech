package org.example.queue;

public interface Producer {

  void produceMessage(String topic, String message);

  void stop();
}
