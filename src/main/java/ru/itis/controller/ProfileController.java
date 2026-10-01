package ru.itis.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itis.dto.form.ProfileForm;
import ru.itis.service.AvatarService;
import ru.itis.service.ProfileService;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final AvatarService avatarService;

    @Autowired
    public ProfileController(ProfileService profileService, AvatarService avatarService) {
        this.profileService = profileService;
        this.avatarService = avatarService;
    }

    @GetMapping("/setup")
    public String getProfileSetupPage(Model model) {
        model.addAttribute("profileForm", new ProfileForm());
        return "profile-setup-page";
    }

    @PostMapping("/setup")
    public String handleProfileSetup(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                     BindingResult bindingResult,
                                     Principal principal,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            return "profile-setup-page";
        }

        try {
            String avatarUrl = avatarService.saveAvatar(profileForm.getAvatarFile());
            profileForm.setAvatarUrl(avatarUrl);
        } catch (Exception e) {
            model.addAttribute("avatarError", e.getMessage());
            return "profile-setup-page";
        }

        profileService.save(profileForm, principal.getName());

        return "redirect:/feed";
    }

    @GetMapping("/my")
    public String showMyProfilePage() {
        return "my-profile-page";
    }
}
