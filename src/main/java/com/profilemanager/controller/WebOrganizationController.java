package com.profilemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organizations")
/** Represents the class component in the SocialNet system. */
public class WebOrganizationController {
    @GetMapping
    public String organizationPage() {
        return "organizations/index";
    }
}
