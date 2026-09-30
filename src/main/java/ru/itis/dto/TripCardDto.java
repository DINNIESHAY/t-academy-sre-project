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
public class TripCardDto {
    private Long id;
    private String destination;
    private String departurePoint;
    @JsonFormat(pattern = "dd MMMM yyyy")
    private LocalDate startAt;
    @JsonFormat(pattern = "dd MMMM yyyy")
    private LocalDate endAt;
    private BigDecimal budget;
    private Integer maxFellows;
    private List<String> tags;

    private String ownerUsername;

    private Boolean isFavorite;

    public static TripCardDto fromEntity(Trip trip) {
        return TripCardDto.builder()
                .id(trip.getId())
                .destination(trip.getDestination())
                .departurePoint(trip.getDeparturePoint())
                .startAt(trip.getStartAt())
                .endAt(trip.getEndAt())
                .budget(trip.getBudget())
                .maxFellows(trip.getMaxFellows())
                .tags(trip.getTags())
                .ownerUsername(trip.getOwner().getUsername())
                .build();
    }
}
