package com.mooveit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.mooveit.controller.PingController;


@SpringBootApplication
// We use direct @Import instead of @ComponentScan to speed up cold starts
// @ComponentScan(basePackages = "com.mooveit.controller")
@Import({ PingController.class })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}