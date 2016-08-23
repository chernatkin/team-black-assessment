package com.epages.microservice.handson.delivery;

import static com.google.common.base.MoreObjects.toStringHelper;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "DELIVERY_ORDER")
public class DeliveryOrder implements Serializable {

    private static final long serialVersionUID = -3563967320907787156L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "ORDER_LINK", length = 255, unique = true, nullable = false)
    private URI orderLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "DELIVERY_ORDER_STATE", length = 30, nullable = false)
    private DeliveryOrderState deliveryOrderState;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public URI getOrderLink() {
        return orderLink;
    }

    public void setOrderLink(URI orderLink) {
        this.orderLink = orderLink;
    }

    public DeliveryOrderState getDeliveryOrderState() {
        return deliveryOrderState;
    }

    public void setDeliveryOrderState(DeliveryOrderState deliveryOrderState) {
        this.deliveryOrderState = deliveryOrderState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeliveryOrder that = (DeliveryOrder) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("id", id).add("orderLink", orderLink).add("deliveryOrderState", deliveryOrderState).toString();
    }
}
