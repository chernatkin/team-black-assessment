package com.epages.microservice.handson.bakery.order;

import java.net.URI;

import static com.google.common.base.MoreObjects.toStringHelper;

public class LineItem {
    private URI pizza;
    private Integer amount;

    public URI getPizza() {
        return pizza;
    }

    public void setPizza(URI pizza) {
        this.pizza = pizza;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("pizza", pizza)
                .add("amount", amount)
                .toString();
    }
}
