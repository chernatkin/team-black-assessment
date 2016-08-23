package com.epages.microservice.handson.bakery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.Optional;

public interface BakeryService {

    Page<BakeryOrder> getAll(Pageable pageable);
    Optional<BakeryOrder> get(Long id);
    Optional<BakeryOrder> getByOrderLink(URI orderLink);
    void acknowledgeOrder(URI orderLink);
    void bakeOrder(URI orderLink);
}
