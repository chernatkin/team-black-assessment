package com.epages.microservice.handson.order;

import java.net.URI;

import javax.money.MonetaryAmount;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.hateoas.ResourceSupport;

public class LineItemResource extends ResourceSupport {

    @NotNull
    private URI pizza;

    @NotNull
    @Min(1)
    private Integer amount;

    private MonetaryAmount price;

    public LineItemResource() {
    }

    public LineItemResource(LineItem lineItem) {
        this.pizza = lineItem.getPizza();
        this.amount = lineItem.getAmount();
        this.price = lineItem.getPrice();
    }

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

    public MonetaryAmount getPrice() {
        return price;
    }

    public void setPrice(MonetaryAmount price) {
        this.price = price;
    }

    public LineItem toEntity() {
        LineItem lineItem = new LineItem();
        lineItem.setPizza(getPizza());
        lineItem.setAmount(getAmount());
        return lineItem;
    }
}
