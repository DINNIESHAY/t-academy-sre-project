package ru.itis.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BusinessMetrics {

    private final Counter loginSuccess;
    private final Counter loginFailure;
    private final Counter registrationStarted;
    private final Counter registrationCompleted;
    private final Counter tripPublished;
    private final Counter applicationSubmitted;
    private final Counter applicationAccepted;
    private final Timer emailSendSuccess;
    private final Timer emailSendFailure;

    public BusinessMetrics(MeterRegistry registry) {
        loginSuccess = loginCounter(registry, "success");
        loginFailure = loginCounter(registry, "failure");
        registrationStarted = registrationCounter(registry, "started");
        registrationCompleted = registrationCounter(registry, "completed");
        tripPublished = Counter.builder("trips.published")
                .description("Published trips")
                .register(registry);
        applicationSubmitted = Counter.builder("trip.applications.submitted")
                .description("Submitted trip applications")
                .register(registry);
        applicationAccepted = Counter.builder("trip.applications.accepted")
                .description("Trip applications accepted by trip owners")
                .register(registry);
        emailSendSuccess = emailTimer(registry, "success");
        emailSendFailure = emailTimer(registry, "failure");
    }

    @EventListener(AuthenticationSuccessEvent.class)
    public void onLoginSuccess() {
        loginSuccess.increment();
    }

    @EventListener(AbstractAuthenticationFailureEvent.class)
    public void onLoginFailure() {
        loginFailure.increment();
    }

    public void registrationStarted() {
        registrationStarted.increment();
    }

    public void registrationCompleted() {
        registrationCompleted.increment();
    }

    public void tripPublished() {
        tripPublished.increment();
    }

    public void applicationSubmitted() {
        applicationSubmitted.increment();
    }

    public void applicationAccepted() {
        applicationAccepted.increment();
    }

    public void verificationEmailSent(boolean success, Duration duration) {
        (success ? emailSendSuccess : emailSendFailure).record(duration);
    }

    private static Counter loginCounter(MeterRegistry registry, String outcome) {
        return Counter.builder("auth.logins")
                .description("User login attempts")
                .tag("outcome", outcome)
                .register(registry);
    }

    private static Counter registrationCounter(MeterRegistry registry, String stage) {
        return Counter.builder("registrations")
                .description("User registrations by funnel stage")
                .tag("stage", stage)
                .register(registry);
    }

    private static Timer emailTimer(MeterRegistry registry, String outcome) {
        return Timer.builder("verification.email.send")
                .description("Time to send a verification email via Resend")
                .tag("outcome", outcome)
                .publishPercentileHistogram()
                .register(registry);
    }
}
