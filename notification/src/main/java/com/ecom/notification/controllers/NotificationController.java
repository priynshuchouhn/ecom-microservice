package com.ecom.notification.controllers;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    @GetMapping("/message")
    @RateLimiter(name = "ecomRateLimiter", fallbackMethod = "fallbackMessage")
    public String message() {
        return "Hello World";
    }

    public String fallbackMessage(RequestNotPermitted ex) {
        return "Rate limit exceeded. Try again later.";
    }
}
