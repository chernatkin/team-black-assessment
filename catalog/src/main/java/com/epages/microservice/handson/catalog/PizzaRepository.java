package com.epages.microservice.handson.catalog;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface PizzaRepository extends PagingAndSortingRepository<Pizza, Long> {
}
