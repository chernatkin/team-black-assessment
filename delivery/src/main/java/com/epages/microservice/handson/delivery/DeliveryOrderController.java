package com.epages.microservice.handson.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/delivery-orders")
@ExposesResourceFor(DeliveryOrder.class)
public class DeliveryOrderController {

    private DeliveryService deliveryService;

    @Autowired
    public DeliveryOrderController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PagedResources<Resource<DeliveryOrder>>> getAll(Pageable pageable, PagedResourcesAssembler<DeliveryOrder> pagedResourcesAssembler) {
        Page<DeliveryOrder> deliveryOrders = deliveryService.getAll(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toResource(deliveryOrders));
    }
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource<DeliveryOrder>> get(@PathVariable Long id) {
        Optional<DeliveryOrder> deliveryOrder = deliveryService.get(id);
        return deliveryOrder
                .map(Resource<DeliveryOrder>::new)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @RequestMapping(path = "/search/findByOrder", method = RequestMethod.GET)
    public ResponseEntity<Resource<DeliveryOrder>> getByOrderLink(@RequestParam URI orderLink) {
        Optional<DeliveryOrder> deliveryOrder = deliveryService.getByOrderLink(orderLink);
        return deliveryOrder
                .map(Resource<DeliveryOrder>::new)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
