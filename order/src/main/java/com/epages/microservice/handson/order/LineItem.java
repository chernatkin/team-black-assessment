package com.epages.microservice.handson.order;

import static com.google.common.base.MoreObjects.toStringHelper;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.net.URI;

import javax.money.MonetaryAmount;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "LINE_ITEM")
public class LineItem implements Serializable {

    private static final long serialVersionUID = 3902307439011218750L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @Basic
    @Column(name = "PIZZA", length = 255, nullable = false)
    private URI pizza;

    @Basic
    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @Basic
    @Column(name = "PRICE", length = 20, nullable = false)
    private MonetaryAmount price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public URI getPizza() {
        return pizza;
    }

    public void setPizza(URI pizza) {
        this.pizza = pizza;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public MonetaryAmount getPrice() {
        return price;
    }

    public void setPrice(MonetaryAmount price) {
        this.price = price;
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
        LineItem lineitem = (LineItem) o;
        return id.equals(lineitem.getId());
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("id", id).add("pizza", pizza).add("amount", amount).toString();
    }
}
