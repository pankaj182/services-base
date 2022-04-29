package com.kpa.test.demo_actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.kpa.test")
@EnableScheduling
@EnableAsync
public class ActuatorDemoApplication {

  public static void main(String[] args) {
        SpringApplication.run(ActuatorDemoApplication.class, args);
    }
}