package com.epages.microservice.handson.shared.event;

import static com.epages.microservice.handson.shared.event.EventPublisher.EVENT_PAYLOAD;
import static com.epages.microservice.handson.shared.event.EventPublisher.EVENT_TYPE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.messaging.handler.annotation.Payload;

import com.epages.microservice.handson.shared.json.JsonMapTypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractEventSubscriber {

    protected final ObjectMapper objectMapper;

    protected final String type;

    protected AbstractEventSubscriber(@NotNull ObjectMapper objectMapper, @NotNull String type) {
        this.objectMapper = checkNotNull(objectMapper);
        this.type = checkNotNull(type);
    }

    protected Map<String, Object> extractEvent(String jsonEvent) {
        try {
            return objectMapper.readValue(jsonEvent, new JsonMapTypeReference());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not deserialize event '%s'", jsonEvent), e);
        }
    }

    @RnDEventListener
    public void consume(@Payload String jsonEvent) {
        final Map<String, Object> event = extractEvent(jsonEvent);
        if (isOwnType(event)) {
            handleOwnType(event);
        } else {
            handleForeignType(event);
        }
    }

    protected String getType(Map<String, Object> event) {
        return event.get(EVENT_TYPE).toString();
    }

    protected boolean isOwnType(Map<String, Object> event) {
        return type.equals(getType(event));
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getPayload(Map<String, Object> event) {
        return (Map<String, Object>) event.get(EVENT_PAYLOAD);
    }

    protected void handleForeignType(Map<String, Object> event) {
        // overwrite me if needed
    }

    protected abstract void handleOwnType(Map<String, Object> event);
}