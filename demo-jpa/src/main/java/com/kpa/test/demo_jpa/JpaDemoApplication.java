package com.kpa.test.demo_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.kpa.test")
@EnableScheduling
@EnableAsync
public class JpaDemoApplication {

  public static void main(String[] args) {
        SpringApplication.run(JpaDemoApplication.class, args);
    }
}
