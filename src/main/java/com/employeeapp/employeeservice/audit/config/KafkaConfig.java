package com.employeeapp.employeeservice.audit.config;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String auditGroupId;

    @Bean
    ProducerFactory<String, EmployeeProfileUpdatedEvent> auditProducerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    KafkaTemplate<String, EmployeeProfileUpdatedEvent> kafkaTemplate(
            ProducerFactory<String, EmployeeProfileUpdatedEvent> auditProducerFactory) {
        return new KafkaTemplate<>(auditProducerFactory);
    }

    @Bean
    ConsumerFactory<String, EmployeeProfileUpdatedEvent> auditConsumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, auditGroupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.employeeapp.employeeservice.audit.event");
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EmployeeProfileUpdatedEvent.class.getName());
        properties.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, EmployeeProfileUpdatedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, EmployeeProfileUpdatedEvent> auditConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, EmployeeProfileUpdatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(auditConsumerFactory);
        return factory;
    }
}
