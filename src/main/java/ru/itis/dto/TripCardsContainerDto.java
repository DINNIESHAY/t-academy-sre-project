package ru.itis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripCardsContainerDto {
    private List<TripCardDto> tripCards;
    private int currentPage;
    private int totalPages;
}
