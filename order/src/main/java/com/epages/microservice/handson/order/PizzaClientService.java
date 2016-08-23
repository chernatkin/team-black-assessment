package com.epages.microservice.handson.order;

import java.net.URI;

public interface PizzaClientService {

    Pizza getPizza(URI pizzaUri);

}
