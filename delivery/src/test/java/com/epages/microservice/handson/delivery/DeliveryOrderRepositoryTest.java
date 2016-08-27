package com.epages.microservice.handson.delivery;

import static org.assertj.core.api.BDDAssertions.then;

import java.net.URI;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DeliveryApplicationTest(activeProfiles = { "test", "DeliveryServiceTest" })
public class DeliveryOrderRepositoryTest {

    private static final URI ORDER_LINK = URI.create("http://localhost/orders/1");

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;
    
    @PersistenceUnit
    private EntityManagerFactory emf;
    
    private EntityManager em;
    
    private AuditReader reader;

    @Before
    public void before(){
        em = emf.createEntityManager();
        reader = AuditReaderFactory.get(em);
    }
    
    @After
    public void cleanup() {
        deliveryOrderRepository.deleteAll();
        
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.createNativeQuery("delete from delivery_order_aud").executeUpdate();
        em.createNativeQuery("delete from revinfo").executeUpdate();
        et.commit();
        
        em.close();
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
        
        List<Number> revisions = reader.getRevisions(DeliveryOrder.class, order.getId());
        then(revisions.size()).isEqualTo(1);
        
        DeliveryOrder orderRev = reader.find(DeliveryOrder.class, order.getId(), revisions.get(0));
        assertDeliveryOrder(orderRev, null, DeliveryOrderState.QUEUED);
    }
    
    @Test
    public void should_update_delivery_order_with_history() {
        
        DeliveryOrder order = deliveryOrderRepository.save(getDeliveryOrder(DeliveryOrderState.QUEUED));
        
        order.setDeliveryOrderState(DeliveryOrderState.IN_PROGRESS);
        deliveryOrderRepository.save(order);
        
        assertDeliveryOrder(order, ORDER_LINK, DeliveryOrderState.IN_PROGRESS);
        
        List<Number> revisions = reader.getRevisions(DeliveryOrder.class, order.getId());
        then(revisions.size()).isEqualTo(2);
        
        DeliveryOrder orderRev = reader.find(DeliveryOrder.class, order.getId(), revisions.get(0));
        assertDeliveryOrder(orderRev, null, DeliveryOrderState.QUEUED);
        
        orderRev = reader.find(DeliveryOrder.class, order.getId(), revisions.get(1));
        assertDeliveryOrder(orderRev, null, DeliveryOrderState.IN_PROGRESS);
    }
    
    @Test
    public void should_update_delivery_order_without_history() {
        
        URI uriForUpdate = URI.create("http://localhost/orders/2");
        
        DeliveryOrder order = deliveryOrderRepository.save(getDeliveryOrder(DeliveryOrderState.QUEUED));
        
        order.setOrderLink(uriForUpdate);
        deliveryOrderRepository.save(order);
        
        assertDeliveryOrder(order, uriForUpdate, DeliveryOrderState.QUEUED);
        
        List<Number> revisions = reader.getRevisions(DeliveryOrder.class, order.getId());
        then(revisions.size()).isEqualTo(1);
        
        DeliveryOrder orderRev = reader.find(DeliveryOrder.class, order.getId(), revisions.get(0));
        assertDeliveryOrder(orderRev, null, DeliveryOrderState.QUEUED);
    }
    
    @Test
    public void should_delete_delivery_order_with_history() {
        
        DeliveryOrder order = deliveryOrderRepository.save(getDeliveryOrder(DeliveryOrderState.QUEUED));
        
        deliveryOrderRepository.save(order);
        deliveryOrderRepository.delete(order);
        
        List<DeliveryOrder> allOrders = deliveryOrderRepository.findAll();
        then(allOrders.size()).isEqualTo(0);
        
        List<Number> revisions = reader.getRevisions(DeliveryOrder.class, order.getId());
        then(revisions.size()).isEqualTo(2);
        
        DeliveryOrder orderRev = reader.find(DeliveryOrder.class, order.getId(), revisions.get(0));
        assertDeliveryOrder(orderRev, null, DeliveryOrderState.QUEUED);
        
        orderRev = reader.find(DeliveryOrder.class, order.getId(), revisions.get(1));
        then(orderRev).isNull();
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
        
        List<Number> revisions = reader.getRevisions(DeliveryOrder.class, order.getId());
        then(revisions.size()).isEqualTo(1);
        
        DeliveryOrder orderRev = reader.find(DeliveryOrder.class, order.getId(), revisions.get(0));
        assertDeliveryOrder(orderRev, null, DeliveryOrderState.QUEUED);
    }
    
    private void assertDeliveryOrder(DeliveryOrder actual, URI orderLink, DeliveryOrderState deliveryOrderState) {
        then(actual.getId()).isNotNull();
        then(actual.getOrderLink()).isEqualTo(orderLink);
        then(actual.getDeliveryOrderState()).isEqualTo(deliveryOrderState);
    }
}
