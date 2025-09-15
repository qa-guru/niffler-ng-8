package guru.qa.niffler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.utils.waiter.MapWithWait;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaService implements Runnable {

  private static final Config CFG = Config.getInstance();
  private static final AtomicBoolean isRun = new AtomicBoolean(false);
  private static final Properties properties = new Properties();
  private static final ObjectMapper om = new ObjectMapper();
  private static final MapWithWait<String,UserJson> store = new MapWithWait<>();

  private final List<String> topics;
  private final Consumer<String, String> consumer;

  static {
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
  }

  public  KafkaService() {
    this(CFG.kafkaTopics());
  }

  public static UserJson getUser(String username) throws InterruptedException {
    return store.get(username, 5000L);
  }

  public static boolean isProduced(String username) throws InterruptedException {
    return store.get(username, 5000L) != null;
  }

  public KafkaService(List<String> topics) {
    this.topics = topics;
    this.consumer = new KafkaConsumer<>(properties);
  }
  @Override
  public void run() {
    try {
      isRun.set(true);
      consumer.subscribe(topics);
      while (isRun.get()) {
        ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String,String> record : records) {
          String stringValue = record.value();
          UserJson userJson = om.readValue(stringValue, UserJson.class);
          store.put(userJson.username(),userJson);
        }
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      consumer.close();
      Thread.currentThread().interrupt();
    }
  }

  public void shutdown() {
    isRun.set(false);
  }



}
