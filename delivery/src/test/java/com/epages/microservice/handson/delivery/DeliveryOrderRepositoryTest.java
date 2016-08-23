package com.epages.microservice.handson.delivery;

import static org.assertj.core.api.BDDAssertions.then;

import java.net.URI;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DeliveryApplicationTest(activeProfiles = { "test", "DeliveryServiceTest" })
public class DeliveryOrderRepositoryTest {

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    private DeliveryOrder deliveryOrder;

    @After
    public  void cleanup() {
        deliveryOrderRepository.deleteAll();
    }

    private void givenDeliveryOrder(DeliveryOrderState deliveryOrderState) {
        deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderLink(URI.create("http://localhost/orders/1"));
        deliveryOrder.setDeliveryOrderState(deliveryOrderState);
    }

    private Long whenDeliveryOrderIsSaved() {
        return deliveryOrderRepository.saveAndFlush(deliveryOrder).getId();
    }

    @Test
    public void should_save_delivery_order() {

        givenDeliveryOrder(DeliveryOrderState.QUEUED);

        Long id = whenDeliveryOrderIsSaved();

        then(id).isNotNull();
    }

    @Test
    public void should_save_delivery_order_with_history() {
        // TODO implement me
    }
}
