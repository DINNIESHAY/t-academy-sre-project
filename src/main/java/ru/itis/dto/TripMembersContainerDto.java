package ru.itis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripMembersContainerDto {
    private List<TripMemberDto> members;
    private int currentPage;
    private int totalPages;
}