package com.epages.microservice.handson.order;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address implements Serializable {

    private static final long serialVersionUID = 2667019347732732455L;

    @Column(name = "FIRSTNAME", length = 255, nullable = false)
    private String firstname;

    @Column(name = "LASTNAME", length = 255, nullable = false)
    private String lastname;

    @Column(name = "STREET", length = 255, nullable = false)
    private String street;

    @Column(name = "CITY", length = 255, nullable = false)
    private String city;

    @Column(name = "POSTALCODE", length = 255, nullable = false)
    private String postalCode;

    @Column(name = "TELEPHONE", length = 255, nullable = false)
    private String telephone;

    @Column(name = "EMAIL", length = 255, nullable = true)
    private String email;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
