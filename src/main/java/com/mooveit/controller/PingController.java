package com.mooveit.controller;

import com.mooveit.service.PingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Map;


@RestController
@EnableWebMvc
public class PingController {

    private final PingService pingService;

    PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public Map<String, String> ping() {
        return pingService.ping();
    }
}
