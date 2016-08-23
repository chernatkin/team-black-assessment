package com.epages.microservice.handson.order;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.epages.microservice.handson.shared.validation.Valid;

@Service
class PizzaClientServiceImpl implements PizzaClientService {

    private final RestTemplate restTemplate;

    @Autowired
    public PizzaClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Valid
    public Pizza getPizza(URI pizzaUri) {
        try {
            return restTemplate.getForObject(pizzaUri, Pizza.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new IllegalArgumentException(String.format("Pizza with URI %s could not be found",pizzaUri), e);
            } else {
                throw e;
            }
        }
    }

}
