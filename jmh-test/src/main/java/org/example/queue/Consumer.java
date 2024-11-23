package org.example.queue;

public interface Consumer {

  void consumeMessage();

  void stop();
}
