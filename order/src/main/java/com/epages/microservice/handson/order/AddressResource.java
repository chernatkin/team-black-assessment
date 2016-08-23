package com.epages.microservice.handson.order;

import javax.validation.constraints.NotNull;

public class AddressResource {

    public AddressResource() {
    }

    public AddressResource(Address address) {
        this.setCity(address.getCity());
        this.setEmail(address.getEmail());
        this.setFirstname(address.getFirstname());
        this.setLastname(address.getLastname());
        this.setPostalCode(address.getPostalCode());
        this.setStreet(address.getStreet());
        this.setTelephone(address.getTelephone());
    }

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private String street;

    @NotNull
    private String city;

    @NotNull
    private String postalCode;

    @NotNull
    private String telephone;

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

    public Address toEntity() {
        Address address = new Address();
        address.setCity(getCity());
        address.setEmail(getEmail());
        address.setFirstname(getFirstname());
        address.setLastname(getLastname());
        address.setPostalCode(getPostalCode());
        address.setStreet(getStreet());
        address.setTelephone(getTelephone());
        return address;
    }

}
