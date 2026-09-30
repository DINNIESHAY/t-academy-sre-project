package ru.itis.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itis.dto.form.ProfileForm;
import ru.itis.service.ProfileService;
import ru.itis.util.FileStorageService;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final FileStorageService fileStorageService;

    @Autowired
    public ProfileController(ProfileService profileService,  FileStorageService fileStorageService) {
        this.profileService = profileService;
        this.fileStorageService = fileStorageService;
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
            String avatarUrl = fileStorageService.saveAvatar(profileForm.getAvatarFile());
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
