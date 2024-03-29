package com.mooveit.controller;

import com.mooveit.service.PingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PingController {

    private final PingService pingService;

    PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public Map<String, String> ping() {
        return pingService.ping();
    }


    @RequestMapping(path = "/pong", method = RequestMethod.GET)
    public String pong() {
        return pingService.pong();
    }
}
