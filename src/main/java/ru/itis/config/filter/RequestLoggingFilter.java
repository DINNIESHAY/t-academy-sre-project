package ru.itis.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "request_id";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final List<String> SKIPPED_PATHS = List.of("/actuator", "/css/", "/js/", "/images/");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return SKIPPED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        long start = System.nanoTime();
        Exception failure = null;
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            failure = e;
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } finally {
            int status = failure != null ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : response.getStatus();
            log.atLevel(levelFor(status))
                    .setCause(failure)
                    .addKeyValue("http.request.method", request.getMethod())
                    .addKeyValue("url.path", request.getRequestURI())
                    .addKeyValue("http.response.status_code", status)
                    .addKeyValue("duration_ms", (System.nanoTime() - start) / 1_000_000)
                    .log("HTTP request completed");
            MDC.remove(REQUEST_ID);
        }
    }

    private static Level levelFor(int status) {
        if (status >= 500) {
            return Level.ERROR;
        }
        if (status >= 400) {
            return Level.WARN;
        }
        return Level.DEBUG;
    }
}
