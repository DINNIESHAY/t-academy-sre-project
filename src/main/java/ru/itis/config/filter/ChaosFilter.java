package ru.itis.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ChaosFilter extends OncePerRequestFilter {

    private final double errorRate;
    private final long delayMs;

    public ChaosFilter(@Value("${chaos.error-rate}") double errorRate,
                       @Value("${chaos.delay-ms}") long delayMs) {
        this.errorRate = errorRate;
        this.delayMs = delayMs;
        if (errorRate > 0 || delayMs > 0) {
            log.warn("Chaos mode enabled: errorRate={}, delayMs={}", errorRate, delayMs);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (errorRate > 0 && ThreadLocalRandom.current().nextDouble() < errorRate) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        chain.doFilter(request, response);
    }
}
