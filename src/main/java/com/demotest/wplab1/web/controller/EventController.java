package com.demotest.wplab1.web.controller;

import com.demotest.wplab1.model.Event;
import com.demotest.wplab1.model.Location;
import com.demotest.wplab1.service.impl.EventServiceImpl;
import com.demotest.wplab1.service.impl.LocationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = {"/events"})
public class EventController {

    private final LocationServiceImpl locationService;
    private final EventServiceImpl eventService;

    public EventController(LocationServiceImpl locationService, EventServiceImpl eventService) {
        this.locationService = locationService;
        this.eventService = eventService;
    }

    @GetMapping
    public String getEventsPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        List<Location> locationList = this.locationService.findAll().orElseThrow(() -> new RuntimeException("There are no locations"));
        model.addAttribute("locations", locationList);
        return "listEvents";
    }

    // TODO THIS IS NOT NEEDED
//    @GetMapping(path = "/add-form")
//    public String getAddEventPage(Model model) {
//        // No need for this
//        List<Location> locationList = this.locationService.findAll().orElseThrow(() -> new RuntimeException("There are no locations"));
//        model.addAttribute("event", new Event());
//        model.addAttribute("locations", locationList);
//        return "addEvent";
//    }

    @GetMapping(path = "/add")
    public String addEvent(Model model) {
        List<Location> locationList = this.locationService.findAll().orElseThrow(() -> new RuntimeException("There are no locations"));
        model.addAttribute("event", new Event());
        model.addAttribute("locations", locationList);
        return "add-event";
    }

    @PostMapping(path = "/add")
    public String saveEvent(@RequestParam String name,
                            @RequestParam String description,
                            @RequestParam Double popularityScore,
                            @RequestParam Long locationId) {
        List<Location> locationList = this.locationService.findAll().orElseThrow(() -> new RuntimeException("There are no locations"));
        Location location = locationList.stream().filter(x -> x.getId().equals(locationId)).findFirst().orElseThrow(() -> new RuntimeException("There is no location with id: " + locationId));
        Event event = new Event(name, description, popularityScore, location);
        this.eventService.addOrUpdateEvent(event);
        return "redirect:/events";
    }

    @GetMapping(path = "/edit-event/{eventId}")
    public String getEditEventForm(@PathVariable Long eventId, Model model) {
        Event event = this.eventService.listAll().orElseThrow(RuntimeException::new).stream().filter(x -> x.getId().equals(eventId)).findFirst().orElse(null);
        if (event == null) {
            return "redirect:/events?error=EventNotFound";
        }
        List<Location> locationList = this.locationService.findAll().orElseThrow(() -> new RuntimeException("There are no locations"));
        model.addAttribute("event", event);
        model.addAttribute("locations", locationList);
        return "add-event";
    }


    @PostMapping(path = "/edit/{eventId}")
    public String editEvent(@PathVariable Long eventId, HttpServletRequest request) {
        Event event = this.eventService.listAll().orElseThrow(RuntimeException::new).stream().filter(x -> x.getId().equals(eventId)).findFirst().orElse(null);
        if (event == null) {
            return "redirect:/events?error=EventNotFound";
        }
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        Double popularityScore = Double.parseDouble(request.getParameter("popularityScore"));
        Long locationId = Long.parseLong(request.getParameter("locationId"));
        Location location = this.locationService.findAll().orElseThrow(RuntimeException::new).stream().filter(x -> x.getId().equals(locationId)).findFirst().orElse(null);

        this.eventService.addOrUpdateEvent(new Event(name, description, popularityScore, location));

        return "redirect:/test";
    }

    @PostMapping(path = "/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Event event = this.eventService.listAll().orElseThrow(() -> new RuntimeException("There is no event with id: " + id)).stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
        if (event != null) {
            System.out.println("Event exists");
        }
        this.eventService.deleteEvent(id);
        return "redirect:/";
    }
}
