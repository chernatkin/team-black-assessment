#!/usr/bin/env bash
curl -X POST -H "Content-Type: application/json" -d '{
  "comment" : "Some comment",
  "deliveryAddress" : {
    "firstname" : "Mathias",
    "lastname" : "Dpunkt",
    "street" : "Somestreet 1",
    "city" : "Hamburg",
    "telephone" : "+49404321343",
    "postalCode" : "22305"
  },
  "items" : [ {
    "amount" : 1,
    "pizza" : "http://192.168.99.100:8082/pizzas/1"
  },
  {
    "amount" : 2,
    "pizza" : "http://192.168.99.100:8082/pizzas/2"
  }]
}

' 'http://192.168.99.100:8081/orders'