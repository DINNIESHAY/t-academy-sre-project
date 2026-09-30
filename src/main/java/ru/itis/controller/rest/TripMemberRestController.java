package ru.itis.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.TripMembersContainerDto;
import ru.itis.service.TripMemberService;

import java.security.Principal;

import static ru.itis.controller.rest.TripRestController.TRIPS_PER_PAGE;

@RestController
@RequestMapping("/api/members")
public class TripMemberRestController {

    private final TripMemberService tripMemberService;

    @Autowired
    public TripMemberRestController(TripMemberService tripMemberService) {
        this.tripMemberService = tripMemberService;
    }

    @GetMapping("/my-joined-trips")
    public TripMembersContainerDto getMyJoinedTrips(
            @PageableDefault(size = TRIPS_PER_PAGE) Pageable pageable,
            Principal principal) {
        return tripMemberService.findTripsByMember(principal.getName(), pageable);
    }

    @GetMapping("/trip/{tripId}")
    public TripMembersContainerDto getMembersByTrip(
            @PathVariable Long tripId,
            @PageableDefault(size = TRIPS_PER_PAGE) Pageable pageable) {
        return tripMemberService.findMembersByTrip(tripId, pageable);
    }
}