# Kafka for Beginners

Cloned from [Kafka for Beginners](https://links.datacumulus.com/apache-kafka-coupon)

# Content
- Basics of Kafka
- Twitter Producer
- ElasticSearch Consumer
- Kafka Streams 101
- Kafka Connect Example

# Kafka CLI Commands

#### Cheatsheet
https://ronnieroller.com/kafka/cheat-sheet

#### Create topic
```sh
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

##### Get list of topics
```sh
kafka-topics --list --zookeeper localhost:2181
```

#### Describe Topic
```sh
kafka-topics --describe --bootstrap-server localhost:9092 --topic my-replicated-topic
```

#### Get list of Consumer Groups
```sh
kafka-consumer-groups  --list --bootstrap-server localhost:9092
```

#### Kafka Console Producer
```sh
kafka-console-producer --broker-list localhost:9092 --topic first_topic
kafka-console-producer --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,
```

#### Kafka Console Consumer
```sh
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,
```

### Mac Start Kafka
```sh
brew services start kafka
kafka-server-start /usr/local/etc/kafka/server.properties
```

### Mac Start Zookeeper
```sh
brew services start zookeeper
zkServer start
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
```

### Mac Installation
tutorialspoint.com/apache_kafka/apache_kafka_installation_steps
```sh
brew cask install java & brew install kafka
```

### Windows Install and Start
https://medium.com/@shaaslam/installing-apache-zookeeper-on-windows-45eda303e835
- Set - ZOOKEEPER_HOME
```sh
zkserver
```

https://medium.com/@shaaslam/installing-apache-kafka-on-windows-495f6f2fd3c8
-For all windows commands replace “sh” with “bat”
```sh
.\bin\windows\kafka-server-start.bat .\config\server.properties
```
