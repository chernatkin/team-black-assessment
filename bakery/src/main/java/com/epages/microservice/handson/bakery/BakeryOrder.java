package com.epages.microservice.handson.bakery;

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
@Table(name = "BAKERY_ORDER")
public class BakeryOrder implements Serializable {

    private static final long serialVersionUID = 4644694270160240621L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "ORDER_LINK", length = 255, unique = true, nullable = false)
    private URI orderLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "BAKERY_ORDER_STATE", length = 30, nullable = false)
    private BakeryOrderState bakeryOrderState;

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

    public BakeryOrderState getBakeryOrderState() {
        return bakeryOrderState;
    }

    public void setBakeryOrderState(BakeryOrderState bakeryOrderState) {
        this.bakeryOrderState = bakeryOrderState;
    }
}
