package com.epages.microservice.handson.bakery;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.net.URI;

public interface BakeryOrderRepository extends PagingAndSortingRepository<BakeryOrder, Long> {

    BakeryOrder findByOrderLink(URI orderLink);
}
