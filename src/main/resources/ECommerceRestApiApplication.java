package com.e_commerce.E_Commerce.REST.API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.coyote.http11.Constants.a;
@EnableJpaAuditing
@SpringBootApplication
public class ECommerceRestApiApplication {




    public static void main(String[] args) {

        SpringApplication.run(ECommerceRestApiApplication.class, args);




    }


}


