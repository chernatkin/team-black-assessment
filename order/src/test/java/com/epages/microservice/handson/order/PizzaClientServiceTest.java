package com.epages.microservice.handson.order;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@OrderApplicationTest
public class PizzaClientServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PizzaClientService pizzaServiceClient;

    private String pizzaSampleResponse = "{\n" +
            "  \"name\": \"Pizza Salami\",\n" +
            "  \"description\": \"The classic - Pizza Salami\",\n" +
            "  \"imageUrl\": \"http://www.sardegna-rustica.de/images/pizza.jpg\",\n" +
            "  \"price\": \"EUR 8.90\"}";

    private String emptyResponse = "{}";

    private MockRestServiceServer mockServer;

    private Pizza pizza;

    @Before
    public void setupContext(){
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void should_get_pizza() throws URISyntaxException {
        givenExistingPizza();

        whenPizzaIsRetrieved();

        then(pizza.getName()).isNotEmpty();
        then(pizza.getPrice()).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_for_non_existing_pizza() throws URISyntaxException {
        givenNonExisingPizza();

        whenPizzaIsRetrieved();

        //then error
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_fail_for_missing_values_in_pizza_body() throws URISyntaxException {
        givenPizzaWithMissingFields();

        whenPizzaIsRetrieved();

        //then error
    }

    @Test(expected = HttpServerErrorException.class)
    public void should_fail_for_server_error() throws URISyntaxException {
        givenErrorOnPizza();

        whenPizzaIsRetrieved();

        //then error
    }

    private void whenPizzaIsRetrieved() throws URISyntaxException {
        pizza = pizzaServiceClient.getPizza(new URI("http://localhost/catalog/1"));
    }

    private void givenExistingPizza() {
        mockServer.expect(
                requestTo("http://localhost/catalog/1")).
                andRespond(withSuccess(pizzaSampleResponse, MediaType.APPLICATION_JSON));
    }

    private void givenNonExisingPizza() {
        mockServer.expect(
                requestTo("http://localhost/catalog/1")).
                andRespond(withStatus(HttpStatus.NOT_FOUND));
    }

    private void givenPizzaWithMissingFields() {
        mockServer.expect(
                requestTo("http://localhost/catalog/1")).
                andRespond(withSuccess(emptyResponse, MediaType.APPLICATION_JSON));
    }

    private void givenErrorOnPizza() {
        mockServer.expect(
                requestTo("http://localhost/catalog/1")).
                andRespond(withServerError());
    }

}
