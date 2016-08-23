package com.epages.microservice.handson.shared.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Locale;

@Converter(autoApply = true)
public class LocalePersistenceConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale entityValue) {
        return entityValue  == null ? null : entityValue.toLanguageTag();
    }

    @Override
    public Locale convertToEntityAttribute(String databaseValue) {
        return databaseValue == null ? null : Locale.forLanguageTag(databaseValue);
    }
}