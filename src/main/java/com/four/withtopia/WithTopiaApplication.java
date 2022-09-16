package com.four.withtopia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class WithTopiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithTopiaApplication.class, args);
    }

}
