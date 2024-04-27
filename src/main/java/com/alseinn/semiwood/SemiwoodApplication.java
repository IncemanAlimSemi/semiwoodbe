package com.alseinn.semiwood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SemiwoodApplication {

    public static void main(String[] args) {
        SpringApplication.run(SemiwoodApplication.class, args);
    }

}
