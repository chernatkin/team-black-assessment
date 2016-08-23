package com.epages.microservice.handson.delivery;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Address {

    private String firstname;
    private String lastname;
    private String street;
    private String city;
    private String postalCode;
    private String telephone;

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

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("Name", firstname + " " + lastname)
                .add("Street", street)
                .add("City", city).toString();
    }
}
