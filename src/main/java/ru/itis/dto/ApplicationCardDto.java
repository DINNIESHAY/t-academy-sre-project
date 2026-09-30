package ru.itis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entity.TripApplication;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCardDto {
    private Long id;
    private Long tripId;
    private Long applicantId;
    private String tripDestination;
    private String applicantUsername;
    private String message;
    private String contacts;

    public static ApplicationCardDto fromEntity(TripApplication application) {
        return ApplicationCardDto.builder()
                .id(application.getId())
                .tripId(application.getTrip().getId())
                .applicantId(application.getApplicant().getId())
                .tripDestination(application.getTrip().getDestination())
                .applicantUsername(application.getApplicant().getUsername())
                .message(application.getMessage())
                .contacts(application.getContacts())
                .build();
    }
}