package ru.itis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {

    @GetMapping("/")
    public String getWelcomePage() {
        return "welcome-page";
    }

    @GetMapping("/feed")
    public String getFeedPage() {
        return "feed-page";
    }

    @GetMapping("/trips/{id}")
    public String getSingleTripPage() {
        return "single-trip-page";
    }

    @GetMapping("/create-trip")
    public String getCreateTripPage() {
        return "create-trip-page";
    }
}
