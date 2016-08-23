package com.epages.microservice.handson.bakery;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.RestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@BakeryApplicationTest
public class BakeryOrderControllerTest {

    @Autowired
    private EntityLinks entityLinks;

    @Autowired
    private BakeryOrderRepository bakeryOrderRepository;

    @Autowired
    private WebApplicationContext context;

    private BakeryOrder bakeryOrder;

    private MockMvc mockMvc;

    private String ordersUri;

    private ResultActions orderResultActions;

    @Before
    public void setupContext() {
        mockMvc = webAppContextSetup(context)
                .apply(documentationConfiguration().uris().withPort(80))
                .build();

        ordersUri = entityLinks.linkToCollectionResource(BakeryOrder.class).expand().getHref();
    }

    @After
    public  void cleanup() {
        bakeryOrderRepository.deleteAll();
    }

    @Test
    public void should_get_all_orders() throws Exception {
        givenBakeryOrder();

        whenAllOrdersRetrieved();

        orderResultActions
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$._embedded.bakeryOrders", hasSize(1)))
                .andExpect(jsonPath("$._embedded.bakeryOrders[0].bakeryOrderState", is(BakeryOrderState.IN_PROGRESS.name())))
                .andDo(document("bakeryorders-list", //
                        responseFields(
                                fieldWithPath("_links").description("<<links,Links>> to other resources"),
                                fieldWithPath("_embedded").description("Embedded <<resources-bakeryorder-get,Bakery Orders>>"),
                                fieldWithPath("page").description("<<paging,Paging>> information")
                )))
        ;
    }

    @Test
    public void should_get_order_by_link() throws Exception {
        givenBakeryOrder();

        whenOrderRetrievedByLink();

        orderResultActions
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.orderLink", is(bakeryOrder.getOrderLink().toString())))
                .andExpect(jsonPath("$.bakeryOrderState", is(bakeryOrder.getBakeryOrderState().name())))
        ;
    }

    @Test
    public void should_get_order() throws Exception {
        givenBakeryOrder();

        whenOrderRetrieved();

        orderResultActions
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.orderLink", is(bakeryOrder.getOrderLink().toString())))
                .andExpect(jsonPath("$.bakeryOrderState", is(bakeryOrder.getBakeryOrderState().name())))
                .andDo(document("bakeryorder-get", //
                        responseFields(
                                fieldWithPath("orderLink").description("order link"),
                                fieldWithPath("bakeryOrderState").description("bakery order status: " 
                                + BakeryOrderState.QUEUED.toString() + ", " + BakeryOrderState.IN_PROGRESS.toString() + ", " + BakeryOrderState.DONE.toString())
                    )))
        ;
    }

    private void whenOrderRetrieved() throws Exception {
        URI uri = linkTo(methodOn(BakeryOrderController.class).get(bakeryOrder.getId())).toUri();
        orderResultActions = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void whenOrderRetrievedByLink() throws Exception {
            URI uri = linkTo(methodOn(BakeryOrderController.class).getByOrderLink(bakeryOrder.getOrderLink())).toUri();
            orderResultActions = mockMvc.perform(get(uri)
                    .accept(MediaType.APPLICATION_JSON));
    }

    private void whenAllOrdersRetrieved() throws Exception {
        orderResultActions = mockMvc.perform(get(ordersUri)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void givenBakeryOrder() throws URISyntaxException {
        bakeryOrder = new BakeryOrder();
        bakeryOrder.setOrderLink(new URI("http://localhost/orders/1"));
        bakeryOrder.setBakeryOrderState(BakeryOrderState.IN_PROGRESS);

        bakeryOrder = bakeryOrderRepository.save(bakeryOrder);
    }
}
