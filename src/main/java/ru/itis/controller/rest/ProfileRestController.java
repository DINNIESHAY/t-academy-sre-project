package ru.itis.controller.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.ProfileDto;
import ru.itis.dto.request.UpdateProfileRequest;
import ru.itis.service.ProfileService;

import java.security.Principal;

@RestController
@RequestMapping("/api/profiles")
public class ProfileRestController {
    private final ProfileService profileService;

    @Autowired
    public ProfileRestController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/my")
    public ProfileDto findMyProfile(Principal principal) {
        return profileService.findByUsername(principal.getName());
    }

    @PutMapping("/my")
    public ProfileDto updateMyProfile(@Valid @RequestBody UpdateProfileRequest request, Principal principal) {
        return profileService.update(request, principal.getName());
    }
}
