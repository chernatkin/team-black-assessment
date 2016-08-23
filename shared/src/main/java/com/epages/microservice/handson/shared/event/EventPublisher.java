package com.epages.microservice.handson.shared.event;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.Map;

import com.epages.microservice.handson.shared.json.JsonMapTypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublisher.class);

    public static final String EVENT_TYPE = "type";

    public static final String EVENT_TIMESTAMP = "timestamp";

    public static final String EVENT_PAYLOAD = "payload";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String type, String jsonPayload) {
        final String event = createEvent(type, jsonPayload);
        LOGGER.info("Publishing event '{}'", event);
        rabbitTemplate.convertAndSend(event);
    }

    public void publish(String type, Map<String, Object> payloadMap) {
        String jsonPayload = null;
        try {
            jsonPayload = objectMapper.writeValueAsString(payloadMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Could not serialize event from payload '%s'", payloadMap), e);
        }
        publish(type, jsonPayload);
    }

    protected String createEvent(String type, String jsonPayload) {
        try {
            return objectMapper.writeValueAsString(ImmutableMap.of( //
                    EVENT_TYPE, type, //
                    EVENT_TIMESTAMP, LocalDateTime.now().toString(), //
                    EVENT_PAYLOAD, objectMapper.readValue(jsonPayload, new JsonMapTypeReference()) //
            ));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not serialize event from payload '%s'", jsonPayload), e);
        }
    }
}
