package com.streaming_examples.random_integer_histogram;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class RandomIntegersProducer {
    private static final int RANGE_MIN = 0, RANGE_MAX = 1000000;
    private static final String TOPIC = "random-integers";

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");

        // The acks config controls the criteria under which requests are considered complete.
        // The "all" setting we have specified will result in blocking on the full commit of the record, the slowest but most durable setting.
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 1000000; i++){
            int randI = getRandomIntegerBetweenRange();
            System.out.println("randI: " + randI);

            // The send() method is asynchronous. When called it adds the record to a buffer of pending record sends and immediately returns.
            // This allows the producer to batch together individual records for efficiency.
            producer.send(new ProducerRecord<String, String>(TOPIC, Integer.toString(randI), Integer.toString(randI)));
        }

        System.out.println("Finished producing!");

        producer.close();
    }

    private static int getRandomIntegerBetweenRange(){
        return (int) ((Math.random()*((RANGE_MAX - RANGE_MIN ) + 1)) + RANGE_MIN);
    }
}
