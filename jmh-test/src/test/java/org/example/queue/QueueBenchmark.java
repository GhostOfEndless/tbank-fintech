package org.example.queue;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
public class QueueBenchmark {

  // RabbitMQ
  private QueueRunner simpleRabbitRunner;
  private QueueRunner loadBalancingRabbitRunner;
  private QueueRunner multipleConsumersRabbitRunner;
  private QueueRunner loadBalancingAndMultipleConsumersRabbitRunner;
  private QueueRunner stressTestRabbitRunner;

  // Kafka KRaft
  private QueueRunner simpleKafkaRunner;
  private QueueRunner loadBalancingKafkaRunner;
  private QueueRunner multipleConsumersKafkaRunner;
  private QueueRunner loadBalancingAndMultipleConsumersKafkaRunner;
  private QueueRunner stressTestKafkaRunner;

  QueueRunnersCreator runnersCreator;

  @Setup(Level.Trial)
  public void setup() {
    runnersCreator = new QueueRunnersCreator();

    // rabbit
    simpleRabbitRunner = runnersCreator.createRunner(1, 1, QueueType.RABBITMQ);
    loadBalancingRabbitRunner = runnersCreator.createRunner(3, 1, QueueType.RABBITMQ);
    multipleConsumersRabbitRunner = runnersCreator.createRunner(1, 3, QueueType.RABBITMQ);
    loadBalancingAndMultipleConsumersRabbitRunner = runnersCreator.createRunner(3, 3, QueueType.RABBITMQ);
    stressTestRabbitRunner = runnersCreator.createRunner(10, 10, QueueType.RABBITMQ);

    // kafka
    simpleKafkaRunner = runnersCreator.createRunner(1, 1, QueueType.KAFKA);
    loadBalancingKafkaRunner = runnersCreator.createRunner(3, 1, QueueType.KAFKA);
    multipleConsumersKafkaRunner = runnersCreator.createRunner(1, 3, QueueType.KAFKA);
    loadBalancingAndMultipleConsumersKafkaRunner = runnersCreator.createRunner(3, 3, QueueType.KAFKA);
    stressTestKafkaRunner = runnersCreator.createRunner(10, 10, QueueType.KAFKA);
  }

  @TearDown(Level.Trial)
  public void tearDown() {
    runnersCreator.clear();
    simpleRabbitRunner.stop();
    loadBalancingRabbitRunner.stop();
    multipleConsumersRabbitRunner.stop();
    loadBalancingAndMultipleConsumersRabbitRunner.stop();
    stressTestRabbitRunner.stop();
    simpleKafkaRunner.stop();
    loadBalancingKafkaRunner.stop();
    multipleConsumersKafkaRunner.stop();
    loadBalancingAndMultipleConsumersKafkaRunner.stop();
    stressTestKafkaRunner.stop();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void simpleRabbitProducerLatency() {
    simpleRabbitRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void simpleRabbitConsumerLatency() {
    simpleRabbitRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void simpleRabbitThroughput() {
    simpleRabbitRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingRabbitConfigProducerLatency() {
    loadBalancingRabbitRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingRabbitConfigConsumerLatency() {
    loadBalancingRabbitRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingRabbitConfigThroughput() {
    loadBalancingRabbitRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void multipleConsumersRabbitConfigProducerLatency() {
    multipleConsumersRabbitRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void multipleConsumersRabbitConfigConsumerLatency() {
    multipleConsumersRabbitRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void multipleConsumersRabbitConfigThroughput() {
    multipleConsumersRabbitRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingAndMultipleConsumersRabbitConfigProducerLatency() {
    loadBalancingAndMultipleConsumersRabbitRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingAndMultipleConsumersRabbitConfigConsumerLatency() {
    loadBalancingAndMultipleConsumersRabbitRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingAndMultipleConsumersRabbitConfigThroughput() {
    loadBalancingAndMultipleConsumersRabbitRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void stressTestRabbitConfigProducerLatency() {
    stressTestRabbitRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void stressTestRabbitConfigConsumerLatency() {
    stressTestRabbitRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void stressTestRabbitConfigThroughput() {
    stressTestRabbitRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void simpleKafkaProducerLatency() {
    simpleKafkaRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void simpleKafkaConsumerLatency() {
    simpleKafkaRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void simpleKafkaThroughput() {
    simpleKafkaRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingKafkaProducerLatency() {
    loadBalancingKafkaRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingKafkaConsumerLatency() {
    loadBalancingKafkaRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingKafkaThroughput() {
    loadBalancingKafkaRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingKafkaConfigProducerLatency() {
    loadBalancingKafkaRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingKafkaConfigConsumerLatency() {
    loadBalancingKafkaRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingKafkaConfigThroughput() {
    loadBalancingKafkaRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingAndMultipleConsumersKafkaConfigProducerLatency() {
    loadBalancingAndMultipleConsumersKafkaRunner.producerList()
        .forEach(Producer::produceMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingAndMultipleConsumersKafkaConfigConsumerLatency() {
    loadBalancingAndMultipleConsumersKafkaRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void loadBalancingAndMultipleConsumersKafkaConfigThroughput() {
    loadBalancingAndMultipleConsumersKafkaRunner.run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void stressTestKafkaConfigConsumerLatency() {
    stressTestKafkaRunner.consumerList()
        .forEach(Consumer::consumeMessage);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void stressTestKafkaConfigProducerLatency() {
    stressTestKafkaRunner.producerList()
        .forEach(Producer::produceMessage);
  }


  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Fork(value = 1)
  @Warmup(iterations = 3)
  @Measurement(iterations = 10)
  public void stressTestKafkaConfigThroughput() {
    stressTestKafkaRunner.run();
  }
}
