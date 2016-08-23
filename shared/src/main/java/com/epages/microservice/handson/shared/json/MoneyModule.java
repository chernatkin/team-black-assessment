package com.epages.microservice.handson.shared.json;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.Money;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class MoneyModule extends SimpleModule {

    private static final long serialVersionUID = -4030029940461575009L;

    private static final MonetaryAmountFormat FORMAT = MonetaryFormats.getAmountFormat(Locale.ROOT);

    public MoneyModule() {
        super(MoneyModule.class.getSimpleName());
        addSerializer(Money.class, new MoneySerializer());
        addValueInstantiator(Money.class, new MoneyInstantiator());
    }

    static class MoneySerializer extends ToStringSerializer {

        private static final long serialVersionUID = -1202921717207273870L;

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(FORMAT.format((Money) value));
        }
    }

    static class MoneyInstantiator extends ValueInstantiator implements Serializable {

        private static final long serialVersionUID = 1947287759440697336L;

        @Override
        public String getValueTypeDesc() {
            return Money.class.toString();
        }

        @Override
        public boolean canCreateFromString() {
            return true;
        }

        @Override
        public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
            return Money.parse(value, FORMAT);
        }
    }
}
