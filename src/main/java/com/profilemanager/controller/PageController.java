package com.profilemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
/** Represents the class component in the SocialNet system. */
public class PageController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/events")
    public String events() {
        return "events";
    }
}

