package com.epages.microservice.handson.shared.jpa;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.epages.microservice.handson.HandsOn;

@Configuration
@EntityScan(basePackageClasses = HandsOn.class)
@EnableJpaRepositories(basePackageClasses = HandsOn.class)
public class JpaAutoConfiguration {

}
