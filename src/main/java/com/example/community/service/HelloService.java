package com.example.community.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String getHelloMessage() {
        return "Hello World!";
    }
}
