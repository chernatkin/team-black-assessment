package com.epages.microservice.handson.order;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.net.URI;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@ExposesResourceFor(Order.class)
@CrossOrigin(exposedHeaders = "Location", value = "*")
public class OrderController {

    private final OrderService orderService;
    private final EntityLinks entityLinks;
    private final OrderResourceAssembler orderResourceAssembler;

    @Autowired
    public OrderController(OrderService orderService,
                           EntityLinks entityLinks,
                           OrderResourceAssembler orderResourceAssembler) {

        this.orderService = orderService;
        this.entityLinks = entityLinks;
        this.orderResourceAssembler = orderResourceAssembler;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OrderResource> get(@PathVariable Long id) {
        return orderService.getOrder(id)
                .map(orderResourceAssembler::toResource)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(method = GET)
    public ResponseEntity<PagedResources<OrderResource>> getAll(Pageable pageable, PagedResourcesAssembler<Order> pagedResourcesAssembler) {
        final Page<Order> orders = orderService.getAll(pageable);
        final PagedResources<OrderResource> body = pagedResourcesAssembler.toResource(orders, orderResourceAssembler);
        return ResponseEntity.ok(body);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> create(@RequestBody @Valid OrderResource orderResource) {
        Order order = new Order();
        order.setDeliveryAddress(orderResource.getDeliveryAddress().toEntity());
        order.setComment(orderResource.getComment());
        order.setItems(orderResource.getOrderItems().stream()
                .map(LineItemResource::toEntity)
                .collect(Collectors.toList()));
        order = orderService.create(order);
        URI location = entityLinks.linkForSingleResource(Order.class, order.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

}
