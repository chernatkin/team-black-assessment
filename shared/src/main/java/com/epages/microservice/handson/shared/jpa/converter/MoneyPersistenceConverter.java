package com.epages.microservice.handson.shared.jpa.converter;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Locale;

@Converter(autoApply = true)
public class MoneyPersistenceConverter implements AttributeConverter<MonetaryAmount, String> {

    private static final MonetaryAmountFormat FORMAT = MonetaryFormats.getAmountFormat(Locale.ROOT);

    @Override
    public String convertToDatabaseColumn(MonetaryAmount attribute) {
        return FORMAT.format(attribute);
    }

    @Override
    public MonetaryAmount convertToEntityAttribute(String dbData) {
        return Money.parse(dbData, FORMAT);
    }
}
