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
public class MyTripCardDto {
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

    private Long likesCount;

    public static MyTripCardDto fromEntity(Trip trip) {
        return MyTripCardDto.builder()
                .id(trip.getId())
                .destination(trip.getDestination())
                .departurePoint(trip.getDeparturePoint())
                .startAt(trip.getStartAt())
                .endAt(trip.getEndAt())
                .budget(trip.getBudget())
                .maxFellows(trip.getMaxFellows())
                .tags(trip.getTags())
                .build();
    }
}
