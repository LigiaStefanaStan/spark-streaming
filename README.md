# Spark Streaming
Project to play around with Spark streaming

## Setting up Kafka
- install Kafka on Mac with brew
    - ```brew install kafka```
    
## Content
### Spark streaming with Kafka
- ```part4integrations``` package
    - playing around with exercises from Udemy course (Apache Spark Streaming 3 with Scala | Rock the JVM)
    
    - running Kafka on Docker container
        - start up the containers: ```docker-compose up```
        - list the containers: ```docker ps```
        - connect to Kafka container: ```docker exec -it rockthejvm-sparkstreaming-kafka bash```
    
    - create a topic:
        - navigate to the bin folder: ```cd /opt/kafka_version/bin```
        - run script: ```kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic <topic_name>```
        
    - create a console producer
        - data written in the console will be fed into Kafka to the topic
        - run script: ```kafka-console-producer.sh --broker-list localhost:9092 --topic <topic_name>```
        - read from the Spark application what is written to the topic
        
    - create a console consumer
        - data written from the Spark application to Kafka will be read from the console
        - run script: ```kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic <topic_name>```

Notes:
- the key determines on which partition the values (messages sent from the console) will be fed into
- values written to Kafka are held in binary 
- for a dataframe to be Kafka compatible, it needs to have a key and a value
- checkpoints directory is important for Kafka caching as it marks the data already being sent to Kafka and ensures 
  data is not sent twice, it will know where the writing was left off and continue from there