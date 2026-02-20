package com.e_commerce.E_Commerce.REST.API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
@EnableJpaAuditing // Add this line
public class ECommerceRestApiApplication {


	public static void main(String[] args) {

        SpringApplication.run(ECommerceRestApiApplication.class, args);
//        Arrays.stream(args).forEach(System.out::println);

    }

}

