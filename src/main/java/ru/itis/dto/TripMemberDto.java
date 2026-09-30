package ru.itis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entity.TripMember;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripMemberDto {
    private Long id;
    private Long tripId;
    private String tripDestination;
    private String tripDeparturePoint;
    private String ownerUsername;
    private String username;

    public static TripMemberDto fromEntity(TripMember member) {
        return TripMemberDto.builder()
                .id(member.getId())
                .tripId(member.getTrip().getId())
                .tripDestination(member.getTrip().getDestination())
                .tripDeparturePoint(member.getTrip().getDeparturePoint())
                .ownerUsername(member.getTrip().getOwner().getUsername())
                .username(member.getUser().getUsername())
                .build();
    }
}