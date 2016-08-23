package com.epages.microservice.handson.delivery;

import java.net.URI;
import java.time.LocalDateTime;

public class DeliveryOrderReceivedEvent {

    private URI orderLink;
    private LocalDateTime estimatedTimeOfDelivery;

    public URI getOrderLink() {
        return orderLink;
    }

    public void setOrderLink(URI orderLink) {
        this.orderLink = orderLink;
    }

    public LocalDateTime getEstimatedTimeOfDelivery() {
        return estimatedTimeOfDelivery;
    }

    public void setEstimatedTimeOfDelivery(LocalDateTime estimatedTimeOfDelivery) {
        this.estimatedTimeOfDelivery = estimatedTimeOfDelivery;
    }
}
