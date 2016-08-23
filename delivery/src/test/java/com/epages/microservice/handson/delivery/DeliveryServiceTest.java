package com.epages.microservice.handson.delivery;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.epages.microservice.handson.delivery.order.Order;

@RunWith(SpringJUnit4ClassRunner.class)
@DeliveryApplicationTest(activeProfiles = { "test", "DeliveryServiceTest" })
@IntegrationTest({ "delivery.timeToPrepareDeliveryInMillis:1", "delivery.timeToDeliverInMillis:1" })
public class DeliveryServiceTest {

    @Autowired
    private DeliveryEventPublisher deliveryEventPublisher;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Captor
    private ArgumentCaptor<DeliveryOrderReceivedEvent> deliveryEventCaptor;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private MockRestServiceServer mockServer;

    private String orderResponse = "{\n" +
            "  \"status\": \"DELIVERED\",\n" +
            "  \"created\": \"2015-09-02T09:07:50.954\",\n" +
            "  \"totalPrice\": \"EUR 28.70\",\n" +
            "  \"orderItems\": [\n" +
            "    {\n" +
            "      \"pizza\": \"http://192.168.99.100:8082/pizzas/1\",\n" +
            "      \"amount\": 1,\n" +
            "      \"price\": \"EUR 8.90\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"deliveryAddress\": {\n" +
            "    \"firstname\": \"Mathias\"\n" +
            "  },\n" +
            "  \"_links\": {\n" +
            "    \"self\": {\n" +
            "      \"href\": \"http://localhost/orders/1\",\n" +
            "      \"templated\": false\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private BakingOrderReceivedEvent bakingOrderReceivedEvent;

    private DeliveryOrder deliveryOrder;
    private CompletableFuture<Boolean> asyncInteractionFuture;

    @Configuration
    @Profile("DeliveryServiceTest")
    public static class MockConfiguration {
        @Bean
        public DeliveryEventPublisher deliveryEventPublisher() {
            return mock(DeliveryEventPublisher.class);
        }
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @After
    public void reset() {
        Mockito.reset(deliveryEventPublisher);
        deliveryOrderRepository.deleteAll();
    }

    @BeforeClass
    public static void configureDeliveryService() {
        System.setProperty("delivery.timeToDeliverInMillis", "1");
        System.setProperty("delivery.timeToPrepareDeliveryInMillis", "1");
    }

    @Test
    public void should_schedule_delivery_and_send_event() throws URISyntaxException {
        givenBakingOrderReceivedEvent();

        //when
        deliveryService.scheduleDelivery(bakingOrderReceivedEvent);

        //then
        verify(deliveryEventPublisher).sendDeliveryOrderReceivedEvent(deliveryEventCaptor.capture());
        then(deliveryEventCaptor.getValue().getEstimatedTimeOfDelivery())
                .isEqualTo(bakingOrderReceivedEvent.getEstimatedTimeOfCompletion().plusNanos(2_000_000));
        then(deliveryService.getByOrderLink(bakingOrderReceivedEvent.getOrderLink()).isPresent()).isTrue();
        then(deliveryService.getByOrderLink(bakingOrderReceivedEvent.getOrderLink()).get()).isNotNull();
        then(deliveryService.getByOrderLink(bakingOrderReceivedEvent.getOrderLink()).get().getDeliveryOrderState())
                .isEqualTo(DeliveryOrderState.QUEUED);
    }

    @Test
    public void should_deliver() throws URISyntaxException, InterruptedException, TimeoutException, ExecutionException {
        givenDeliveryOrder();

        givenMockedEventPublisher();

        whenDeliveryStarted();

        //then
        verify(deliveryEventPublisher).sendDeliveredEvent(orderCaptor.capture());
        then(orderCaptor.getValue().getOrderLink()).isEqualTo(deliveryOrder.getOrderLink());
        then(deliveryOrder.getDeliveryOrderState()).isEqualTo(DeliveryOrderState.DONE);

    }

    private void whenDeliveryStarted() throws InterruptedException, ExecutionException, TimeoutException {
        deliveryService.startDelivery(deliveryOrder.getOrderLink());

        //wait for async interaction until timeout
        asyncInteractionFuture.get(1000, TimeUnit.MILLISECONDS);

        deliveryOrder = deliveryOrderRepository.findOne(deliveryOrder.getId());
    }

    private void givenMockedEventPublisher() {
        asyncInteractionFuture = new CompletableFuture<>();
        mockServer.expect(requestTo(deliveryOrder.getOrderLink())).andRespond(withSuccess(orderResponse, MediaType.APPLICATION_JSON));

        //complete the future when deliveryEventPublisher.sendDeliveredEvent is called
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                asyncInteractionFuture.complete(true);
                return null;
            }
        }).when(deliveryEventPublisher).sendDeliveredEvent(any());

    }

    private void givenDeliveryOrder() throws URISyntaxException {
        URI orderUri = new URI("http://localhost/orders/1");
        deliveryOrder = new DeliveryOrder();
        deliveryOrder.setDeliveryOrderState(DeliveryOrderState.QUEUED);
        deliveryOrder.setOrderLink(orderUri);
        deliveryOrder = deliveryOrderRepository.save(deliveryOrder);
    }

    private void givenBakingOrderReceivedEvent() throws URISyntaxException {
        bakingOrderReceivedEvent = new BakingOrderReceivedEvent();
        bakingOrderReceivedEvent.setOrderLink(new URI("http://localhost/orders/1"));
        bakingOrderReceivedEvent.setEstimatedTimeOfCompletion(LocalDateTime.now());
    }
}
