package ru.itis.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.PendingRegistration;
import ru.itis.dto.form.RegistrationForm;
import ru.itis.entity.User;
import ru.itis.model.UserRole;
import ru.itis.service.UserService;
import ru.itis.service.VerificationService;

import java.util.Collections;
import java.util.Set;

@Controller
@RequestMapping("/auth")
public class AuthorizationController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationService verificationService;

    @Autowired
    public AuthorizationController(UserService userService,
                                   BCryptPasswordEncoder bCryptPasswordEncoder,
                                   VerificationService verificationService) {
        this.userService = userService;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.verificationService = verificationService;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model,
                               @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("isAuthenticationFailed", true);
        }
        return "login-page";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registrationForm",  new RegistrationForm());
        return "register-page";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                               BindingResult bindingResult,
                               Model model,
                               HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "register-page";
        }

        if (userService.usernameExists(form.getUsername())) {
            model.addAttribute("usernameAlreadyExists", true);
            return "register-page";
        }

        if (userService.emailExists(form.getEmail())) {
            model.addAttribute("emailAlreadyExists", true);
            return "register-page";
        }

        String verificationCode = verificationService.sendVerificationCode(form.getEmail());

        PendingRegistration pendingRegistration = new PendingRegistration(
                form.getUsername(),
                form.getEmail(),
                passwordEncoder.encode(form.getPassword())
        );

        session.setAttribute("pendingRegistration", pendingRegistration);
        session.setAttribute("emailVerificationCode", verificationCode);

        return "redirect:/auth/verify-code";
    }

    @GetMapping("/verify-code")
    public String getVerifyCodePage(HttpSession session) {
        if (session.getAttribute("emailVerificationCode") == null) {
            return "redirect:/auth/register";
        }
        return "verify-code-page";
    }

    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam("code") String userCode,
                             HttpSession session,
                             Model model) {

        String sessionCode = (String) session.getAttribute("emailVerificationCode");
        PendingRegistration pendingRegistration = (PendingRegistration) session.getAttribute("pendingRegistration");

        if (sessionCode == null || pendingRegistration == null) {
            return "redirect:/auth/register";
        }

        if (sessionCode.equals(userCode)) {
            User user = new User(
                    pendingRegistration.username(),
                    pendingRegistration.email(),
                    pendingRegistration.encodedPassword(),
                    UserRole.USER
            );

            userService.save(user);
            forceAutoLogin(pendingRegistration.username(), pendingRegistration.encodedPassword(), session);

            session.removeAttribute("emailVerificationCode");
            session.removeAttribute("pendingRegistration");

            return "redirect:/profile/setup";
        }

        model.addAttribute("wrongCode", true);
        return "verify-code-page";
    }

    private void forceAutoLogin(String username, String password, HttpSession session) {
        Set<SimpleGrantedAuthority> roles = Collections.singleton(UserRole.USER.toAuthority());
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, roles);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
