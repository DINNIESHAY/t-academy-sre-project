package ru.itis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entity.Trip;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripDto {
    private Long id;
    private String destination;
    private String departurePoint;
    @JsonFormat(pattern = "dd MMMM yyyy")
    private LocalDate startAt;
    @JsonFormat(pattern = "dd MMMM yyyy")
    private LocalDate endAt;
    private BigDecimal budget;
    private Integer maxFellows;
    private String description;
    private List<String> tags;

    private Long ownerId;
    private String ownerUsername;

    public static TripDto fromEntity(Trip trip){
        return TripDto.builder()
                .id(trip.getId())
                .destination(trip.getDestination())
                .departurePoint(trip.getDeparturePoint())
                .startAt(trip.getStartAt())
                .endAt(trip.getEndAt())
                .budget(trip.getBudget())
                .maxFellows(trip.getMaxFellows())
                .description(trip.getDescription())
                .tags(trip.getTags())
                .ownerId(trip.getOwner().getId())
                .ownerUsername(trip.getOwner().getUsername())
                .build();
    }
}
