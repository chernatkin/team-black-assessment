package com.epages.microservice.handson.bakery;

import java.net.URI;
import java.time.LocalDateTime;

public class BakeryOrderReceivedEvent {

    private URI orderLink;
    private LocalDateTime estimatedTimeOfCompletion;

    public URI getOrderLink() {
        return orderLink;
    }

    public void setOrderLink(URI orderLink) {
        this.orderLink = orderLink;
    }

    public LocalDateTime getEstimatedTimeOfCompletion() {
        return estimatedTimeOfCompletion;
    }

    public void setEstimatedTimeOfCompletion(LocalDateTime estimatedTimeOfCompletion) {
        this.estimatedTimeOfCompletion = estimatedTimeOfCompletion;
    }
}
