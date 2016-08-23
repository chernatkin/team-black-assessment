package com.epages.microservice.handson.bakery;

import com.epages.microservice.handson.bakery.order.Order;
import com.epages.microservice.handson.bakery.order.OrderServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BakeryServiceImpl implements BakeryService {

    private BakeryEventPublisher bakeryEventPublisher;
    private BakeryOrderRepository bakeryOrderRepository;
    private OrderServiceClient orderServiceClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(BakeryServiceImpl.class);

    @Value("${bakery.timeToBakePizzaInMillis:1}")
    private Long timeToBakePizzaInMillis;

    @Autowired
    public BakeryServiceImpl(BakeryEventPublisher bakeryEventPublisher,
                             BakeryOrderRepository bakeryOrderRepository,
                             OrderServiceClient orderServiceClient) {
        this.bakeryEventPublisher = bakeryEventPublisher;
        this.bakeryOrderRepository = bakeryOrderRepository;
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public Page<BakeryOrder> getAll(Pageable pageable) {
        return bakeryOrderRepository.findAll(pageable);
    }

    @Override
    public Optional<BakeryOrder> get(Long id) {
        return Optional.ofNullable(bakeryOrderRepository.findOne(id));
    }

    @Override
    public Optional<BakeryOrder> getByOrderLink(URI orderLink) {
        return Optional.ofNullable(bakeryOrderRepository.findByOrderLink(orderLink));
    }

    @Override
    public void acknowledgeOrder(URI orderLink) {
        saveBakeryOrder(orderLink);

        sendBakingOrderReceivedEvent(orderLink);
    }

    @Async("bakeryThreadPoolTaskExecutor")
    public void bakeOrder(URI orderLink) {
        BakeryOrder bakeryOrder = bakeryOrderRepository.findByOrderLink(orderLink);
        if (bakeryOrder == null) {
            LOGGER.warn("Could not find BakeryOrder with uri {}", orderLink);
            return;
        }
        updateOrderState(bakeryOrder, BakeryOrderState.IN_PROGRESS);

        //retrieve the order to get the pizzas to bake
        Order order = orderServiceClient.getOrder(orderLink);
        //do all the work
        bake(order);

        updateOrderState(bakeryOrder, BakeryOrderState.DONE);
        bakeryEventPublisher.sendBakingFinishedEvent(orderLink);
    }

    private void updateOrderState(BakeryOrder bakeryOrder, BakeryOrderState orderState) {
        bakeryOrder.setBakeryOrderState(orderState);
        bakeryOrderRepository.save(bakeryOrder);
    }

    private void sendBakingOrderReceivedEvent(URI orderLink) {
        BakeryOrderReceivedEvent bakeryOrderReceivedEvent = new BakeryOrderReceivedEvent();
        bakeryOrderReceivedEvent.setOrderLink(orderLink);
        bakeryOrderReceivedEvent.setEstimatedTimeOfCompletion(estimateTimeOfCompletion());
        bakeryEventPublisher.sendBakingOrderReceivedEvent(bakeryOrderReceivedEvent);
    }

    private LocalDateTime estimateTimeOfCompletion() {
        return LocalDateTime.now().plusNanos(timeToBakePizzaInMillis * 1_000_000);
    }

    private void saveBakeryOrder(URI orderLink) {
        BakeryOrder bakeryOrder = new BakeryOrder();
        bakeryOrder.setBakeryOrderState(BakeryOrderState.QUEUED);
        bakeryOrder.setOrderLink(orderLink);
        bakeryOrderRepository.save(bakeryOrder);
    }

    private void bake(Order order) {
        Assert.notNull(order, "order may not be null");
        LOGGER.info("Working hard to bake order {} with items {}", order.getOrderLink(), order.getOrderItems());
        try {
            Thread.sleep(timeToBakePizzaInMillis);
        } catch (InterruptedException e) {
        }
    }
}
