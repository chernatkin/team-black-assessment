package com.epages.microservice.handson.bakery;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/", produces = { "application/hal+json", "application/json" })
public class IndexController {

    private final Set<Class<?>> entitiesWithController;
    private EntityLinks entityLinks;
    private RelProvider relProvider;

    @Autowired
    public IndexController(ListableBeanFactory beanFactory, EntityLinks entityLinks, RelProvider relProvider) {
        this.entityLinks = entityLinks;
        this.relProvider = relProvider;
        Map<String, Object> beansWithExposesResourceForAnnotation = beanFactory
                .getBeansWithAnnotation(ExposesResourceFor.class);
        entitiesWithController = beansWithExposesResourceForAnnotation.values().stream()
                .map(o -> o.getClass().getAnnotation(ExposesResourceFor.class).value()).collect(Collectors.toSet());
    }

    public static class ControllerLinksResource extends ResourceSupport {
    }

    @RequestMapping(method = GET)
    public ResponseEntity<ControllerLinksResource> getControllerLinks() {
        ControllerLinksResource controllerLinksResource = new ControllerLinksResource();
        controllerLinksResource.add(linkTo(methodOn(IndexController.class).getControllerLinks()).withSelfRel());

        entitiesWithController.forEach(entityClass -> controllerLinksResource //
                .add(entityLinks.linkToCollectionResource(entityClass) //
                        .withRel(relProvider.getCollectionResourceRelFor(entityClass))));

        return ResponseEntity.ok(controllerLinksResource);
    }
}
