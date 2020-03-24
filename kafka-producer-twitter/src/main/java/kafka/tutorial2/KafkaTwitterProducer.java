package com.streaming_examples.twitter_word_counter;

import com.streaming_examples.Utils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.concurrent.LinkedBlockingQueue;

public class KafkaTwitterProducer {
    private static final String topicName = "twitter-stream";
    private static final String[] keyWords = new String[]{"obama", "trump"};

    public static void main(String[] args) throws Exception {
        Properties twitterTokens = getTwitterTokens();
        LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<>(1000);
        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true)
            .setOAuthConsumerKey(twitterTokens.getProperty("twitter.consumerKey"))
            .setOAuthConsumerSecret(twitterTokens.getProperty("twitter.consumerSecret"))
            .setOAuthAccessToken(twitterTokens.getProperty("twitter.accessToken"))
            .setOAuthAccessTokenSecret(twitterTokens.getProperty("twitter.accessTokenSecret"));

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                queue.offer(status);
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());

                for (URLEntity url : status.getURLEntities()) {
                    System.out.println(url.getDisplayURL());
                }

                for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
                    System.out.println(hashtagEntity.getText());
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        twitterStream.addListener(listener);

        FilterQuery query = new FilterQuery().track(keyWords);
        twitterStream.filter(query);

        Thread.sleep(5000);

        //Add Kafka producer config settings
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serializa-tion.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serializa-tion.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        int i = 0, j = 0;

        while (i < 10) {
            Status ret = queue.poll();

            if (ret == null) {
                Thread.sleep(100);
                i++;
            } else {
                for (HashtagEntity hashtagEntity : ret.getHashtagEntities()) {
                    System.out.println("Hashtag: " + hashtagEntity.getText());
                    producer.send(new ProducerRecord<>(
                            topicName, Integer.toString(j++), hashtagEntity.getText()));
                }
            }
        }
        producer.close();
        Thread.sleep(5000);
        twitterStream.shutdown();
    }

    private static Properties getTwitterTokens(){
        Properties prop = new Properties();

        try(InputStream input = new FileInputStream("resources/twitterTokens.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return prop;
    }
}