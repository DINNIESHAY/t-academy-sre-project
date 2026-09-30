package ru.itis.controller.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.MyTripCardsContainerDto;
import ru.itis.dto.TripCardsContainerDto;
import ru.itis.dto.TripDto;
import ru.itis.dto.request.AddTripRequest;
import ru.itis.dto.request.UpdateTripRequest;
import ru.itis.service.TripService;

import java.security.Principal;

@RestController
@RequestMapping("/api/trips")
public class TripRestController {

    public static final int TRIPS_PER_PAGE = 5;

    private final TripService tripService;

    @Autowired
    public TripRestController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public TripCardsContainerDto findAll(@PageableDefault(size = TRIPS_PER_PAGE) Pageable pageable, Principal principal) {
        return tripService.findAll(pageable, principal.getName());
    }

    @GetMapping("/my")
    public MyTripCardsContainerDto findAllMyTrips(@PageableDefault(size = TRIPS_PER_PAGE) Pageable pageable, Principal principal) {
        return tripService.findAllByUsername(principal.getName(), pageable);
    }

    @GetMapping("/{id}")
    public TripDto findById(@PathVariable("id") Long id) {
        return tripService.findById(id);
    }

    @PostMapping
    public TripDto save(@Valid @RequestBody AddTripRequest request, Principal principal) {
        return tripService.save(request, principal.getName());
    }

    @PutMapping("/{id}")
    public TripDto update(@Valid @RequestBody UpdateTripRequest request, @PathVariable("id") Long id) {
        return tripService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        tripService.delete(id);
    }
}
