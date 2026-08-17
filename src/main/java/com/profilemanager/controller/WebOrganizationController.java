package com.profilemanager.controller;

import com.profilemanager.model.Event;
import com.profilemanager.model.Organization;
import com.profilemanager.service.EventService;
import com.profilemanager.service.OrganizationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/organizations")
public class WebOrganizationController {

    private final OrganizationService orgService;
    private final EventService eventService;

    public WebOrganizationController(OrganizationService orgService, EventService eventService) {
        this.orgService = orgService;
        this.eventService = eventService;
    }

    @GetMapping
    public String listOrganizations(Model model) {
        List<Organization> orgs = orgService.listAll();
        model.addAttribute("organizations", orgs);
        return "organizations/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("organization", new Organization());
        return "organizations/create";
    }

    @PostMapping("/create")
    public String createOrganization(@ModelAttribute Organization org) {
        orgService.create(org);
        return "redirect:/organizations";
    }

    @GetMapping("/{id}")
    public String viewOrganization(@PathVariable UUID id, Model model) {
        Organization org = orgService.getById(id);
        List<Event> events = eventService.findByOrganization(id);
        model.addAttribute("org", org);
        model.addAttribute("events", events);
        return "organizations/detail";
    }

    @GetMapping("/{id}/events/create")
    public String showEventForm(@PathVariable UUID id, Model model) {
        Event event = new Event();
        event.setOrganizationId(id);
        model.addAttribute("event", event);
        model.addAttribute("orgId", id);
        return "events/create";
    }

    @PostMapping("/{id}/events/create")
    public String createEvent(@PathVariable UUID id, @ModelAttribute Event event) {
        event.setOrganizationId(id);
        eventService.create(event);
        return "redirect:/organizations/" + id;
    }
}
