package com.kuit.kupage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KupageApplication {

    public static void main(String[] args) {
        SpringApplication.run(KupageApplication.class, args);
    }

}
