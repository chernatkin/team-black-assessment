package com.epages.microservice.handson.bakery.order;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Order {

    private URI orderLink;

    private List<LineItem> orderItems = new ArrayList<>();

    public URI getOrderLink() {
        return orderLink;
    }

    @JsonProperty("_links")
    @JsonDeserialize(using = OrderLinkDeserializer.class)
    public void setOrderLink(URI orderLink) {
        this.orderLink = orderLink;
    }

    public List<LineItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<LineItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("orderItems", orderItems).toString();
    }

    static class OrderLinkDeserializer extends JsonDeserializer<URI> {
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
