package com.epages.microservice.handson.delivery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.Optional;

public interface DeliveryService {

    Page<DeliveryOrder> getAll(Pageable pageable);
    Optional<DeliveryOrder> get(Long id);
    Optional<DeliveryOrder> getByOrderLink(URI orderLink);

    void startDelivery(URI orderUri);

    void scheduleDelivery(BakingOrderReceivedEvent event);
}
