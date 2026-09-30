package ru.itis.controller.rest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.ApplicationCardsContainerDto;
import ru.itis.dto.request.AddApplicationRequest;
import ru.itis.service.TripApplicationService;

import java.security.Principal;
import java.util.Map;

import static ru.itis.controller.rest.TripRestController.TRIPS_PER_PAGE;

@RestController
@RequestMapping("/api/applications")
@Slf4j
public class TripApplicationRestController {

    private final TripApplicationService tripApplicationService;

    @Autowired
    public TripApplicationRestController(TripApplicationService tripApplicationService) {
        this.tripApplicationService = tripApplicationService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createApplication(@RequestParam Long tripId,
                                               @Valid @RequestBody AddApplicationRequest request,
                                               Principal principal) {
        try {
            tripApplicationService.createApplication(tripId, principal.getName(), request);
            return ResponseEntity.ok().body(Map.of("message", "Заявка успешно отправлена!"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Failed to create application for trip {} by user {}: {}", tripId, principal.getName(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating application for trip {} by user {}", tripId, principal.getName(), e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Ошибка при отправке заявки"));
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptApplication(@PathVariable("id") Long applicationId, Principal principal) {
        try {
            tripApplicationService.acceptApplication(applicationId);
            return ResponseEntity.ok().body(Map.of("message", "Заявка принята"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Failed to accept application {} by user {}: {}", applicationId, principal.getName(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error accepting application {} by user {}", applicationId, principal.getName(), e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Ошибка при принятии заявки"));
        }
    }

    @GetMapping("/to-my-trips")
    public ApplicationCardsContainerDto getApplicationsToMyTrips(
            @PageableDefault(size = TRIPS_PER_PAGE) Pageable pageable,
            Principal principal) {
        return tripApplicationService.findApplicationsToMyTrips(principal.getName(), pageable);
    }
}