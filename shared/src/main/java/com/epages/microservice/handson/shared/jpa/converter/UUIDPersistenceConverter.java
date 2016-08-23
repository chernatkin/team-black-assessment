package com.epages.microservice.handson.shared.jpa.converter;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true)
public class UUIDPersistenceConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(UUID entityValue) {
        return (entityValue == null) ? null : entityValue.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String databaseValue) {
        return (StringUtils.hasLength(databaseValue) ? UUID.fromString(databaseValue.trim()) : null);
    }
}
