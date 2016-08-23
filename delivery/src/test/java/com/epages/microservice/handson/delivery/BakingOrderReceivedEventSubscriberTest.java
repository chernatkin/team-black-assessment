package com.epages.microservice.handson.delivery;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DeliveryApplicationTest(activeProfiles = {"test", "BakingOrderReceivedEventSubscriberTest"})
public class BakingOrderReceivedEventSubscriberTest {

    @Autowired
    private BakingOrderReceivedEventSubscriber bakingOrderReceivedEventSubscriber;

    @Autowired
    private DeliveryEventPublisher deliveryEventPublisher;

    @Captor
    private ArgumentCaptor<DeliveryOrderReceivedEvent> deliveryEventCaptor;

    private String event = "{\n" +
            "  \"type\" : \"BakingOrderReceived\",\n" +
            "  \"timestamp\" : \"2015-09-01T21:43:48.391\",\n" +
            "  \"payload\" : {\n" +
            "      \"orderLink\" : \"http://192.168.99.100:8081/orders/1\",\n" +
            "      \"estimatedTimeOfCompletion\" : \"2015-09-12T07:43:48.391\"\n" +
            "  }\n" +
            "}";

    @Configuration
    @Profile("BakingOrderReceivedEventSubscriberTest")
    public static class MockConfiguration {
        @Bean
        public DeliveryEventPublisher deliveryEventPublisher() {
            return mock(DeliveryEventPublisher.class);
        }
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void resetMocks() {
        Mockito.reset(deliveryEventPublisher);
    }

    @Test
    public void should_schedule_delivery_and_publish_event() {
        bakingOrderReceivedEventSubscriber.consume(event);

        verify(deliveryEventPublisher).sendDeliveryOrderReceivedEvent(deliveryEventCaptor.capture());
        then(deliveryEventCaptor.getValue().getOrderLink()).isNotNull();
        then(deliveryEventCaptor.getValue().getOrderLink().toString()).endsWith("1");
        then(deliveryEventCaptor.getValue().getEstimatedTimeOfDelivery()).isNotNull();
    }

}
