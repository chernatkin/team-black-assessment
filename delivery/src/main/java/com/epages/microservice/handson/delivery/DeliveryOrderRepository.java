package com.epages.microservice.handson.delivery;

import java.net.URI;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {

    DeliveryOrder findByOrderLink(URI orderLink);
}
