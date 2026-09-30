package ru.itis.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RedirectAuthenticatedFilter extends OncePerRequestFilter {
    private final List<String> guestUrls = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/verify-code"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (guestUrls.contains(requestUri)) {
                response.sendRedirect(request.getContextPath() + "/feed");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
