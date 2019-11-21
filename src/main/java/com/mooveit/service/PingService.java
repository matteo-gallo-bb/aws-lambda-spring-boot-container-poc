package com.mooveit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PingService {

    private final String response;

    public PingService(@Value("${welcome.message:Hello}") String response){
        this.response = response;
    }


    @Cacheable("pong")
    public Map<String, String> ping() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", response);
        return pong;
    }

}
