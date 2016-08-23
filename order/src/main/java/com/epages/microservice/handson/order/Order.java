package com.epages.microservice.handson.order;

import static com.google.common.base.MoreObjects.toStringHelper;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.money.MonetaryAmount;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.javamoney.moneta.Money;

import com.google.common.base.Objects;

@Entity
@Table(name = "PIZZA_ORDER")
public class Order implements Serializable {

    private static final long serialVersionUID = -3578518882422353855L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Basic
    @Column(name = "ORDERED_AT", nullable = false)
    private LocalDateTime orderedAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Basic
    @Column(name = "ETD", nullable = true)
    private LocalDateTime estimatedTimeOfDelivery;

    @Access(AccessType.FIELD)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LineItem> items = new ArrayList<>();

    @Basic
    @Column(name = "COMMENT", length = 255)
    private String comment;

    @Embedded
    private Address deliveryAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getEstimatedTimeOfDelivery() {
        return estimatedTimeOfDelivery;
    }

    public void setEstimatedTimeOfDelivery(LocalDateTime estimatedTimeOfDelivery) {
        this.estimatedTimeOfDelivery = estimatedTimeOfDelivery;
    }

    public List<LineItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<LineItem> items) {
        this.items.clear();
        items.forEach(this::addItem);
    }

    public void addItem(LineItem item) {
        item.setOrder(this);
        this.items.add(item);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public MonetaryAmount getTotalPrice() {
        return items.stream().map(lineItem -> lineItem.getPrice().multiply(lineItem.getAmount())).reduce(MonetaryAmount::add)
                .orElse(Money.of(0.0, "EUR"));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return id.equals(order.getId());
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("id", id).add("items", items).toString();
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }
}
