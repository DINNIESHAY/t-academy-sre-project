package ru.itis.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.TripCardsContainerDto;
import ru.itis.service.FavoriteService;

import java.security.Principal;

import static ru.itis.controller.rest.TripRestController.TRIPS_PER_PAGE;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteRestController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteRestController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public TripCardsContainerDto findAllMyFavorites(@PageableDefault(size = TRIPS_PER_PAGE) Pageable pageable, Principal principal){
        String username = principal.getName();
        return favoriteService.findFavoriteTrips(username, pageable);
    }

    @PostMapping("/toggle")
    public ResponseEntity<Void> toggleFavorite(@RequestParam Long tripId, Principal principal) {
        String currentUsername = principal.getName();
        favoriteService.toggleFavorite(tripId, currentUsername);

        return ResponseEntity.ok().build();
    }
}
