package org.padan.Model.KafkaSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.padan.Model.Objects.Reservation;

import java.util.Map;
import java.util.Properties;

public class KafkaRentalProducer {
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper mapper;

    public KafkaRentalProducer() {
        Properties producerConfig = new Properties();

        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "local");
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9094,localhost:9096");
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");

        this.producer = new KafkaProducer<>(producerConfig);
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public void sendReservation(Reservation reservation) {
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "rentalName", "Padan Rental",
                    "reservationData", reservation
            ));

            producer.send(new ProducerRecord<>("reservationsTopic", reservation.getReservationId().toString(), json),
                    (data, e) -> {
                        if (e == null) {
                            System.out.println("Wysłano rezerwację do kafki.");
                        } else {
                            System.out.println("Nie udało się wysłać rezerwacji do kafki: " + e.getMessage());
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        producer.close();
    }
}