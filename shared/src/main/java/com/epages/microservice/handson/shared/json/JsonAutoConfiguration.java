package com.epages.microservice.handson.shared.json;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Locale.ENGLISH;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.geo.GeoModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JsonAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(GeoModule geoModule) {
        return Jackson2ObjectMapperBuilder //
                .json() //
                .locale(ENGLISH) //
                .timeZone("UTC") //
                .indentOutput(true) //
                .serializationInclusion(NON_NULL) //
                .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS, FAIL_ON_UNKNOWN_PROPERTIES) //
                .modulesToInstall(geoModule) //
                ;
    }

    @Bean
    public Module moneyModule(Map<String, ObjectMapper> objectMappers) {
        MoneyModule moneyModule = new MoneyModule();
        objectMappers.forEach((beanName, objectMapper) -> objectMapper.registerModule(moneyModule));
        return moneyModule;
    }
}
