package org.padan.Model.KafkaSetup;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaRentalConsumer implements Runnable {
    private final KafkaConsumer<String, String> consumer;
    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public KafkaRentalConsumer(String id) {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9094,localhost:9096");
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, id);

        this.consumer = new KafkaConsumer<>(consumerConfig);
        this.consumer.subscribe(Collections.singletonList("reservationsTopic"));

        this.client = MongoClients.create("mongodb://admin:adminpassword@localhost:27017,localhost:27018,localhost:27019/?authSource=admin&replicaSet=replica_set_single");
        this.collection = client.getDatabase("rentafield").getCollection("reservations");
    }

    @Override
    public void run() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        Document doc = Document.parse(record.value());
                        doc.put("_id", record.key());
                        collection.replaceOne(Filters.eq("_id", record.key()), doc, new ReplaceOptions().upsert(true));
                        System.out.println("Zapisano do kafki (konsument otrzymał dane)");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
            client.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}