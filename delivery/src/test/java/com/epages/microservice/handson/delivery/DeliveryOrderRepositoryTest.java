package com.epages.microservice.handson.delivery;

import static org.assertj.core.api.BDDAssertions.then;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.epages.microservice.handson.shared.jpa.EntityAudit;
import com.epages.microservice.handson.shared.jpa.EntityAuditRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@DeliveryApplicationTest(activeProfiles = { "test", "DeliveryServiceTest" })
public class DeliveryOrderRepositoryTest {

    private static final URI ORDER_LINK = URI.create("http://localhost/orders/1");
    
    private static final String DELIVERY_ORDER_TABLE_NAME = "DELIVERY_ORDER";
    
    private static final String DELIVERY_ORDER_STATE_FIELD_NAME = "DELIVERY_ORDER_STATE";
    
    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private EntityAuditRepository entityAuditRepository;
    
    @After
    public  void cleanup() {
        deliveryOrderRepository.deleteAll();
        entityAuditRepository.deleteAll();
    }

    private DeliveryOrder getDeliveryOrder(DeliveryOrderState deliveryOrderState) {
        return getDeliveryOrder(deliveryOrderState, ORDER_LINK);
    }
    
    private DeliveryOrder getDeliveryOrder(DeliveryOrderState deliveryOrderState, URI uri) {
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderLink(uri);
        deliveryOrder.setDeliveryOrderState(deliveryOrderState);
        return deliveryOrder;
    }

    @Test
    public void should_save_delivery_order() {
        
        DeliveryOrder order = deliveryOrderRepository.saveAndFlush(getDeliveryOrder(DeliveryOrderState.QUEUED));

        //check that event listeners did not affect on saved object
        assertDeliveryOrder(order, ORDER_LINK, DeliveryOrderState.QUEUED);
    }

    @Test
    public void should_save_delivery_order_with_history() {
        
        DeliveryOrder order = deliveryOrderRepository.save(getDeliveryOrder(DeliveryOrderState.QUEUED));
        
        assertDeliveryOrder(order, ORDER_LINK, DeliveryOrderState.QUEUED);
        
        List<EntityAudit> allAudit = entityAuditRepository.findAll();
        
        then(allAudit.size()).isEqualTo(1);
        assertAudit(allAudit.get(0), 
                    DELIVERY_ORDER_TABLE_NAME, 
                    DELIVERY_ORDER_STATE_FIELD_NAME, 
                    order.getId(), 
                    DeliveryOrderState.QUEUED.name());
    }
    
    @Test
    public void should_update_delivery_order_with_history() {
        
        DeliveryOrder order = deliveryOrderRepository.save(getDeliveryOrder(DeliveryOrderState.QUEUED));
        
        order.setDeliveryOrderState(DeliveryOrderState.IN_PROGRESS);
        deliveryOrderRepository.save(order);
        
        assertDeliveryOrder(order, ORDER_LINK, DeliveryOrderState.IN_PROGRESS);
        
        List<EntityAudit> allAudit = entityAuditRepository.findAll();
        
        then(allAudit.size()).isEqualTo(2);
        allAudit = allAudit.stream()
                           .sorted((a1, a2) -> Long.compare(a1.getId(), a2.getId()))
                           .collect(Collectors.toList());
        
        assertAudit(allAudit.get(0), 
                    DELIVERY_ORDER_TABLE_NAME, 
                    DELIVERY_ORDER_STATE_FIELD_NAME, 
                    order.getId(), 
                    DeliveryOrderState.QUEUED.name());
        
        assertAudit(allAudit.get(1), 
                    DELIVERY_ORDER_TABLE_NAME, 
                    DELIVERY_ORDER_STATE_FIELD_NAME, 
                    order.getId(), 
                    DeliveryOrderState.IN_PROGRESS.name());
    }
    
    @Test
    public void should_update_delivery_order_without_history() {
        
        URI uriForUpdate = URI.create("http://localhost/orders/2");
        
        DeliveryOrder order = deliveryOrderRepository.save(getDeliveryOrder(DeliveryOrderState.QUEUED));
        
        order.setOrderLink(uriForUpdate);
        deliveryOrderRepository.save(order);
        
        assertDeliveryOrder(order, uriForUpdate, DeliveryOrderState.QUEUED);
        
        List<EntityAudit> allAudit = entityAuditRepository.findAll();
        
        then(allAudit.size()).isEqualTo(1);
        
        assertAudit(allAudit.get(0), 
                    DELIVERY_ORDER_TABLE_NAME, 
                    DELIVERY_ORDER_STATE_FIELD_NAME, 
                    order.getId(), 
                    DeliveryOrderState.QUEUED.name());
    }
    
    @Test
    public void should_delete_delivery_order_with_history() {
        
        DeliveryOrder order = deliveryOrderRepository.save(getDeliveryOrder(DeliveryOrderState.QUEUED));
        
        deliveryOrderRepository.save(order);
        deliveryOrderRepository.delete(order);
        
        List<DeliveryOrder> allOrders = deliveryOrderRepository.findAll();
        then(allOrders.size()).isEqualTo(0);
        
        List<EntityAudit> allAudit = entityAuditRepository.findAll();
        then(allAudit.size()).isEqualTo(2);
        
        allAudit = allAudit.stream()
                           .sorted((a1, a2) -> Long.compare(a1.getId(), a2.getId()))
                           .collect(Collectors.toList());
        
        assertAudit(allAudit.get(0), 
                    DELIVERY_ORDER_TABLE_NAME, 
                    DELIVERY_ORDER_STATE_FIELD_NAME, 
                    order.getId(), 
                    DeliveryOrderState.QUEUED.name());
        
        assertAudit(allAudit.get(1), 
                    DELIVERY_ORDER_TABLE_NAME, 
                    DELIVERY_ORDER_STATE_FIELD_NAME, 
                    order.getId(), 
                    null);
    }
    
    @Test
    public void should_not_update_delivery_order_without_history() {
        
        DeliveryOrder order = getDeliveryOrder(DeliveryOrderState.QUEUED);
        deliveryOrderRepository.save(order);
        
        order.setOrderLink(null);
        order.setDeliveryOrderState(DeliveryOrderState.IN_PROGRESS);
        try {
            deliveryOrderRepository.save(order);
            Assert.fail();
        } catch(DataIntegrityViolationException ve) {
        }
        
        order = deliveryOrderRepository.findOne(order.getId());
        assertDeliveryOrder(order, ORDER_LINK, DeliveryOrderState.QUEUED);
        
        List<EntityAudit> allAudit = entityAuditRepository.findAll();
        then(allAudit.size()).isEqualTo(1);

        assertAudit(allAudit.get(0), 
                    DELIVERY_ORDER_TABLE_NAME, 
                    DELIVERY_ORDER_STATE_FIELD_NAME, 
                    order.getId(), 
                    DeliveryOrderState.QUEUED.name());
    }
    
    private void assertDeliveryOrder(DeliveryOrder actual, URI orderLink, DeliveryOrderState deliveryOrderState) {
        then(actual.getId()).isNotNull();
        then(actual.getOrderLink()).isEqualTo(orderLink);
        then(actual.getDeliveryOrderState()).isEqualTo(deliveryOrderState);
    }
    
    private void assertAudit(EntityAudit actual, String entityName, String fieldName, Long entityId, String state) {
        then(actual.getEntityName()).isEqualTo(entityName);
        then(actual.getEntityId()).isEqualTo(entityId);
        then(actual.getFieldName()).isEqualTo(fieldName);
        then(actual.getState()).isEqualTo(state);
        then(actual.getId()).isNotNull();
    }
}
