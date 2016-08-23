package com.epages.microservice.handson.catalog;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.money.MonetaryAmount;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PIZZA")
public class Pizza {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Basic
    @Column(name = "NAME", length = 75, nullable = false, unique = true)
    private String name;

    @Basic
    @Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;

    @Basic
    @Column(name = "IMAGE_URL", length = 255, nullable = false)
    private String imageUrl;

    @Basic
    @Column(name = "PRICE")
    private MonetaryAmount price;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "PIZZA_TOPPINGS",
            joinColumns = @JoinColumn(name = "PIZZA_ID", nullable = false),
            uniqueConstraints = @UniqueConstraint(name = "U_PIZZA_TOPPINGS_PIZZA_ID_TOPPING", columnNames = { "PIZZA_ID", "TOPPING" }))
    @Enumerated(EnumType.STRING)
    @Column(name = "TOPPING", length = 30, nullable = false)
    private Set<Topping> toppings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MonetaryAmount getPrice() {
        return price;
    }

    public void setPrice(MonetaryAmount price) {
        this.price = price;
    }

    public Set<Topping> getToppings() {
        return toppings;
    }

    public void setToppings(Set<Topping> toppings) {
        this.toppings = toppings;
    }
}
