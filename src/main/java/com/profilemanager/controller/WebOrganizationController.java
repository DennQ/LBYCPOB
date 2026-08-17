package com.profilemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organizations")
public class WebOrganizationController {
    @GetMapping
    public String organizationPage() {
        return "organizations/index";
    }
}
