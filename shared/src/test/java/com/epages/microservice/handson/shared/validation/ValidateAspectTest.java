package com.epages.microservice.handson.shared.validation;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.epages.microservice.handson.shared.event.EventAutoConfiguration;
import com.epages.microservice.handson.shared.jpa.JpaAutoConfiguration;
import com.epages.microservice.handson.shared.json.JsonAutoConfiguration;
import com.epages.microservice.handson.shared.web.WebAutoConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ValidateAspectTest.ValidationTestApp.class)
public class ValidateAspectTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {
            EventAutoConfiguration.class,
            JpaAutoConfiguration.class,
            JsonAutoConfiguration.class,
            WebAutoConfiguration.class
    })
    @ComponentScan
    static class ValidationTestApp {

        public static void main(String[] args) {
            SpringApplication.run(ValidationTestApp.class, args);
        }

        @Bean
        public ValidateAspect validateAspectBean(Validator validator) {
            return new ValidateAspect(validator);
        }

        @Bean
        public DummyService dummyService() {
            return new DummyServiceImpl();
        }
    }

    static class Data {
        @NotNull
        String s;
    }

    interface DummyService {
        void testNull();

        void testInputParam(Data d);

        Data testOutputParam();

        Data testNonAnnotatedMethod();
    }

    static class DummyServiceImpl implements DummyService {

        public DummyServiceImpl() {
        }

        @Valid
        public void testNull() {
            return;
        }

        @Valid
        public void testInputParam(@Valid Data d) {
            return;
        }

        @Valid
        public Data testOutputParam() {
            return new Data();
        }

        public Data testNonAnnotatedMethod() {
            return new Data();
        }

    }

    @Autowired
    private DummyService dummyService;

    @Test
    public void should_not_fail_on_null() {
        dummyService.testNull();
    }

    @Test
    public void should_not_fail_on_non_annotated_method() {
        dummyService.testNonAnnotatedMethod();
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_fail_on_invalid_input_parameter() {
        dummyService.testInputParam(new Data());
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_fail_on_invalid_output_parameter() {
        dummyService.testOutputParam();
    }

    @Test
    public void should_not_fail_on_valid_input_parameter() {
        Data d = new Data();
        d.s = "test";
        dummyService.testInputParam(d);
    }

}