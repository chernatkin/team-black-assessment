package com.epages.microservice.handson.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.money.MonetaryAmount;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.hateoas.ResourceSupport;

public class OrderResource extends ResourceSupport {

    private OrderStatus status;

    private LocalDateTime created;

    private LocalDateTime estimatedTimeOfDelivery;

    private MonetaryAmount totalPrice;

    @Valid
    private List<LineItemResource> orderItems = new ArrayList<>();

    private String comment;

    @Valid
    @NotNull
    private AddressResource deliveryAddress;

    public OrderResource() {
    }

    public OrderResource(Order order) {
        this.status = order.getStatus();
        this.created = order.getOrderedAt();
        this.totalPrice = order.getTotalPrice();
        this.estimatedTimeOfDelivery = order.getEstimatedTimeOfDelivery();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getEstimatedTimeOfDelivery() {
        return estimatedTimeOfDelivery;
    }

    public void setEstimatedTimeOfDelivery(LocalDateTime estimatedTimeOfDelivery) {
        this.estimatedTimeOfDelivery = estimatedTimeOfDelivery;
    }

    public MonetaryAmount getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(MonetaryAmount totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<LineItemResource> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<LineItemResource> orderItems) {
        this.orderItems = orderItems;
    }

    public AddressResource getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddressResource deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
