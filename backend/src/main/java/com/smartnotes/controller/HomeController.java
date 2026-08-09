package com.smartnotes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {

        Map<String, Object> response = new HashMap<>();

        response.put("application", "Smart Notes API");
        response.put("status", "UP");
        response.put("version", "1.0.0");
        response.put("message", "Smart Notes backend is running successfully");

        return response;
    }
}