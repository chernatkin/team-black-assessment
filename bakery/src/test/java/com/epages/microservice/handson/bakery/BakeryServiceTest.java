package com.epages.microservice.handson.bakery;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import com.epages.microservice.handson.bakery.order.Order;

@RunWith(SpringJUnit4ClassRunner.class)
@BakeryApplicationTest(activeProfiles = {"test", "BakeryServiceTest"})
@IntegrationTest("bakery.timeToBakePizzaInMillis:1")
public class BakeryServiceTest {

    @Autowired
    private BakeryEventPublisher bakeryEventPublisher;

    @Autowired
    private BakeryService bakeryService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BakeryOrderRepository bakeryOrderRepository;

    @Captor
    private ArgumentCaptor<BakeryOrderReceivedEvent> bakeryEventCaptor;

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
            "      \"href\": \"http://192.168.99.100:8081/orders/1\",\n" +
            "      \"templated\": false\n" +
            "    }\n" +
            "  }\n" +
            "}";


    private BakeryOrder bakeryOrder;
    private CompletableFuture<Boolean> asyncInteractionFuture;
    private URI orderUri;

    @Configuration
    @Profile("BakeryServiceTest")
    public static class MockConfiguration {
        @Bean
        public BakeryEventPublisher bakeryEventPublisher() {
            return mock(BakeryEventPublisher.class);
        }
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @After
    public void reset() {
        bakeryOrderRepository.deleteAll();
    }

    @Test
    public void should_acknowledge_order_and_send_event() throws URISyntaxException {
        givenOrderReceivedEvent();

        //when
        bakeryService.acknowledgeOrder(orderUri);

        //then
        verify(bakeryEventPublisher).sendBakingOrderReceivedEvent(bakeryEventCaptor.capture());
        then(bakeryEventCaptor.getValue().getEstimatedTimeOfCompletion()).isNotNull();
        then(bakeryEventCaptor.getValue().getOrderLink()).isEqualTo(orderUri);
        then(bakeryService.getByOrderLink(orderUri).isPresent()).isTrue();
        then(bakeryService.getByOrderLink(orderUri).get().getBakeryOrderState()).isEqualTo(BakeryOrderState.QUEUED);
    }

    @Test
    public void should_bake() throws URISyntaxException, InterruptedException, TimeoutException, ExecutionException {
        givenBakeryOrder();
        givenMockedEventPublisher();

        whenBakingStarted();

        //then
        verify(bakeryEventPublisher).sendBakingFinishedEvent(bakeryOrder.getOrderLink());
        then(bakeryOrder.getBakeryOrderState()).isEqualTo(BakeryOrderState.DONE);

    }

    private void givenOrderReceivedEvent() throws URISyntaxException {
        orderUri = new URI("http://localhost/orders/1");
    }

    private void whenBakingStarted() throws InterruptedException, ExecutionException, TimeoutException {
        bakeryService.bakeOrder(bakeryOrder.getOrderLink());

        //wait for async interaction until timeout
        asyncInteractionFuture.get(1000, TimeUnit.MILLISECONDS);

        bakeryOrder = bakeryOrderRepository.findOne(bakeryOrder.getId());
    }

    private void givenMockedEventPublisher() {
        asyncInteractionFuture = new CompletableFuture<>();
        mockServer.expect(requestTo(bakeryOrder.getOrderLink())).andRespond(withSuccess(orderResponse, MediaType.APPLICATION_JSON));

        //complete the future when deliveryEventPublisher.sendDeliveredEvent is called
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                asyncInteractionFuture.complete(true);
                return null;
            }
        }).when(bakeryEventPublisher).sendBakingFinishedEvent(orderUri);


    }

    private void givenBakeryOrder() throws URISyntaxException {
        orderUri = new URI("http://localhost/orders/1");
        bakeryOrder = new BakeryOrder();
        bakeryOrder.setBakeryOrderState(BakeryOrderState.QUEUED);
        bakeryOrder.setOrderLink(orderUri);
        bakeryOrder = bakeryOrderRepository.save(bakeryOrder);
    }
}
