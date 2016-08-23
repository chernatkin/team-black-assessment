package com.epages.microservice.handson.order;

import javax.validation.Validator;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import com.epages.microservice.handson.shared.validation.ValidateAspect;

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableRabbit // TODO really necessary? check shared EventAutoConfiguration
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public ValidateAspect validateAspectBean(Validator validator) {
        return new ValidateAspect(validator);
    }

}
