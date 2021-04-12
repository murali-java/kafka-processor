package com.kafkaprocessor.kafkaprocessor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Arrays;
import java.util.Properties;

@Configuration
public class KafkaStreamConfig {

    public static final String SOURCE_TOPIC = "TextLinesTopic";
    public static final String DEST_TOPIC = "WordsWithCountsTopic";

    @Bean
    @Qualifier("textLinesTopic")
    public NewTopic textLinesTopic() {
        return TopicBuilder.name(SOURCE_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    @Qualifier("wordsWithCountsTopic")
    public NewTopic wordsWithCountsTopic() {
        return TopicBuilder.name(DEST_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean(destroyMethod = "close")
    public KafkaStreams wordCountStream(){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream(SOURCE_TOPIC);
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
        wordCounts.toStream().to(DEST_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return streams;
    }
}
