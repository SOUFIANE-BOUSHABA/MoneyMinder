package com.example.moneyminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneyMinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyMinderApplication.class, args);
    }

}
