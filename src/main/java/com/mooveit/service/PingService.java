package com.mooveit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PingService {

    private final String response;
    private final String pongServiceUrl;

    public PingService(@Value("${welcome.message:Hello}") String response,
                       @Value("${pong.service.url}") String pongServiceUrl) {
        this.response = response;
        this.pongServiceUrl = pongServiceUrl;
    }

    public Map<String, String> ping() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", response);
        return pong;
    }

    public String pong() {
        return new RestTemplate().getForObject(pongServiceUrl, String.class);
    }

}
