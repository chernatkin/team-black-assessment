package com.epages.microservice.handson.order;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.RestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
@RunWith(SpringJUnit4ClassRunner.class)
@OrderApplicationTest(activeProfiles = {"test", "OrderControllerTest"})
public class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityLinks entityLinks;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;

    private ResultActions ordersResultAction;

    private String ordersUri;

    private String jsonInput;

    private String pizzaSampleResponse = "{\n" +
            "  \"name\": \"Pizza Salami\",\n" +
            "  \"description\": \"The classic - Pizza Salami\",\n" +
            "  \"imageUrl\": \"http://www.sardegna-rustica.de/images/pizza.jpg\",\n" +
            "  \"price\": \"EUR 8.90\"}";

    private Order order;

    @Configuration
    @Profile("OrderControllerTest")
    public static class OrderEventPublisherMockConfiguration {

        @Bean
        public OrderEventPublisher orderEventPublisher() {
            return mock(OrderEventPublisher.class);
        }
    }

    @Before
    public void setupContext(){
        mockMvc = webAppContextSetup(context)
                .apply(documentationConfiguration().uris().withPort(80))
                .build();

        //mock the rest call made b< OrderServiceImpl to PizzaServiceClient
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(
                requestTo("http://localhost/catalog/1")).
                andRespond(withSuccess(pizzaSampleResponse, MediaType.APPLICATION_JSON));

        ordersUri = linkTo(methodOn(OrderController.class).getAll(null, null)).toUri().toString();

        orderRepository.deleteAll();

        reset(orderEventPublisher);
    }

    @Test
    public void should_create_order() throws Exception {
        givenInputData();

        whenOrderCreated();

        ordersResultAction
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(header().string(HttpHeaders.LOCATION, startsWith(ordersUri)))
                .andDo(document("order-create", //
                        requestFields( //
                                fieldWithPath("comment").description("delivery comment"), //
                                fieldWithPath("orderItems[].amount").description("how many pizzas do you eat today?"), //
                                fieldWithPath("orderItems[].pizza").description("which pizza do you want?"), //
                                fieldWithPath("deliveryAddress.firstname").description("Your first name"), //
                                fieldWithPath("deliveryAddress.lastname").description("Your last name"), //
                                fieldWithPath("deliveryAddress.street").description("Your stree"), //
                                fieldWithPath("deliveryAddress.city").description("Your city"), //
                                fieldWithPath("deliveryAddress.postalCode").description("Your postal code"), //
                                fieldWithPath("deliveryAddress.telephone").description("Your telephone"), //
                                fieldWithPath("deliveryAddress.email").description("Your email address").optional() //
                )))
        ;

        verify(orderEventPublisher).sendOrderCreatedEvent(order);
    }

    @Test
    public void should_get_order() throws Exception {
        givenExistingOrder();

        whenOrderRetrieved();

        ordersResultAction
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status", is(order.getStatus().name())))
                .andExpect(jsonPath("$.totalPrice", notNullValue()))
                .andExpect(jsonPath("$.orderItems", hasSize(order.getItems().size())))
                .andExpect(jsonPath("$.deliveryAddress.firstname", is(order.getDeliveryAddress().getFirstname())))
                .andExpect(jsonPath("$._links.self.href",
                        is(entityLinks.linkForSingleResource(Order.class, order.getId()).toUri().toString())))

                .andDo(document("order-get",
                        responseFields(
                                fieldWithPath("status").description("order status"),
                                fieldWithPath("created").description("creation timestamp"),
                                fieldWithPath("totalPrice").description("Total order amount"),
                                fieldWithPath("orderItems[].pizza").description("ordered pizza"),
                                fieldWithPath("orderItems[].amount").description("Amount of pizzas"),
                                fieldWithPath("orderItems[].price").description("Price (Currency symbol and numeric value)"),
                                fieldWithPath("deliveryAddress").description("delivery address as POSTed when <<resources-order-create,creating an Order>>"),
                                fieldWithPath("_links").description("<<links,Links>> to other resources")
                        ))) //
        ;
    }

    @Test
    public void should_get_all_orders() throws Exception {
        givenExistingOrder();

        whenAllOrdersRetrieved();

        ordersResultAction
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(document("orders-list",
                        responseFields(
                                fieldWithPath("_embedded").description("Current page of <<resources-order-get,Orders>>"),
                                fieldWithPath("page").description("<<paging,Paging>> information"),
                                fieldWithPath("_links").description("<<links,Links>> to other resources")
                        ))) //
        ;
    }
    private void whenAllOrdersRetrieved() throws Exception {
        ordersResultAction = mockMvc.perform(get(ordersUri)
                .accept(MediaTypes.HAL_JSON));
    }

    private void whenOrderRetrieved() throws Exception {
        URI orderUri = entityLinks.linkForSingleResource(Order.class, order.getId()).toUri();

        ordersResultAction = mockMvc.perform(get(orderUri)
                .accept(MediaTypes.HAL_JSON));
    }

    private void givenExistingOrder() throws URISyntaxException {
        Order orderTmp = new Order();
        orderTmp.setComment("some comment");
        Address address = new Address();
        address.setCity("Hamburg");
        address.setFirstname("Mathias");
        address.setLastname("Dpunkt");
        address.setPostalCode("22222");
        address.setStreet("Pilatuspool 2");
        address.setTelephone("+4908154711");
        orderTmp.setDeliveryAddress(address);

        LineItem lineItem = new LineItem();
        lineItem.setAmount(2);
        lineItem.setPizza(new URI("http://localhost/catalog/1"));

        orderTmp.addItem(lineItem);

        order = orderService.create(orderTmp);
    }

    private void whenOrderCreated() throws Exception {
        ordersResultAction = mockMvc.perform(post(ordersUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInput));

        order = orderService.getAll(null).iterator().next();
    }

    private void givenInputData() throws JsonProcessingException {
        ImmutableMap<Object, Object> address = ImmutableMap.builder().putAll(ImmutableMap.of(
                "firstname", "Mathias",
                "lastname", "Dpunkt",
                "street", "Somestreet 1",
                "city", "Hamburg",
                "telephone", "+49404321343"
        )).put("postalCode", "22305") //
          .put("email", "your@email.address") //
        .build();

        jsonInput = objectMapper.writeValueAsString(ImmutableMap.of(
                "comment", "Some comment",
                "deliveryAddress", address,
                "orderItems", ImmutableList.of(ImmutableMap.of(
                                "amount", 1,
                                "pizza", "http://localhost/catalog/1"
                        )
                )
        ));
    }
}
