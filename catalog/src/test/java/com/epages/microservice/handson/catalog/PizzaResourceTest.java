package com.epages.microservice.handson.catalog;

import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.RestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@CatalogApplicationTest
public class PizzaResourceTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private ResultActions resultAction;

    private String collectionUri;

    private String singleItemUri;

    @Autowired
    private EntityLinks entityLinks;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration().uris().withPort(80)).build();
        collectionUri = entityLinks.linkToCollectionResource(Pizza.class).expand().getHref();
        singleItemUri = entityLinks.linkToSingleResource(Pizza.class, "1").getHref();
    }

    @Test
    public void should_get_all_pizzas() throws Exception {
        givenExistingPizzas();
        whenAllPizzasRetrieved();

        resultAction
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(document("pizzas-list",
                        responseFields(
                                fieldWithPath("_embedded").description("Embedded list of <<resources-pizza-get, pizzas>>."),
                                fieldWithPath("_links").description("<<links, Links>> to other resources.")
                        )));
    }

    @Test
    public void should_get_one_pizzas() throws Exception {
        givenExistingPizzas();
        whenPizzaRetrieved();

        resultAction
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(document("pizza-get",
                        responseFields(
                                fieldWithPath("name").description("Pizza name."),
                                fieldWithPath("description").description("Pizza details."),
                                fieldWithPath("imageUrl").description("Image."),
                                fieldWithPath("price").description("pricate (Currency symbol and numeric value)."),
                                fieldWithPath("toppings[]").description("List of toppings."),
                                fieldWithPath("_links").description("<<links, Links>> to other resources.")
                        )));
    }

    private void whenAllPizzasRetrieved() throws Exception {
        resultAction = mockMvc.perform(get(collectionUri)
                .accept(MediaTypes.HAL_JSON));
    }

    private void whenPizzaRetrieved() throws Exception {
        resultAction = mockMvc.perform(get(singleItemUri)
                .accept(MediaTypes.HAL_JSON));
    }

    private void givenExistingPizzas() {
    }
}
