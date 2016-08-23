package com.epages.microservice.handson.shared.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.amqp.core.ExchangeTypes.FANOUT;
import static com.epages.microservice.handson.shared.event.EventAutoConfiguration.RND_EVENTS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Target(METHOD)
@Retention(RUNTIME)
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        value = "",
                        durable = "false",
                        autoDelete = "true",
                        exclusive = "true"),
                exchange = @Exchange(
                        type = FANOUT,
                        value = RND_EVENTS,
                        durable = "true",
                        autoDelete = "false"),
                key = "#"))
public @interface RnDEventListener {
}
