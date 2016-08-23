package com.epages.microservice.handson.delivery.order;

import java.io.IOException;
import java.net.URI;

import com.epages.microservice.handson.delivery.Address;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Order {

    private URI orderLink;

    private Address deliveryAddress;

    private String comment;

    public URI getOrderLink() {
        return orderLink;
    }

    @JsonProperty("_links")
    @JsonDeserialize(using = OrderLinkDeserializer.class)
    public void setOrderLink(URI orderLink) {
        this.orderLink = orderLink;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    static class OrderLinkDeserializer extends JsonDeserializer<URI>{
        @Override
        public URI deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            final ObjectCodec codec = jsonParser.getCodec();
            final JsonNode links = codec.readTree(jsonParser);
            final JsonNode self = links.get("self");
            final JsonNode href = self.get("href");
            return URI.create(href.textValue());
        }
    }
}
