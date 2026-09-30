package ru.itis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itis.dto.*;
import ru.itis.dto.request.AddTripRequest;
import ru.itis.dto.request.UpdateTripRequest;
import ru.itis.entity.Trip;
import ru.itis.entity.User;
import ru.itis.exception.exceptions.TripNotFoundException;
import ru.itis.exception.exceptions.UserNotFoundException;
import ru.itis.repository.FavoriteRepository;
import ru.itis.repository.TripRepository;
import ru.itis.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    @Autowired
    public TripService(TripRepository tripRepository, UserRepository userRepository, FavoriteRepository favoriteRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Cacheable(value = "tripPages", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':user:' + #username")
    public TripCardsContainerDto findAll(Pageable pageable, String username) {
        Page<Trip> tripPage = tripRepository.findAll(pageable);

        Set<Long> favoriteTripIds = favoriteRepository.findAllByUsername(username);

        List<TripCardDto> tripCards = tripPage.getContent().stream()
                .map(trip -> {
                    TripCardDto dto = TripCardDto.fromEntity(trip);
                    dto.setIsFavorite(favoriteTripIds.contains(trip.getId()));
                    return dto;
                })
                .toList();

        return new TripCardsContainerDto(tripCards,  tripPage.getNumber(), tripPage.getTotalPages());
    }

    @Cacheable(value = "userTrips", key = "#username + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public MyTripCardsContainerDto findAllByUsername(String username, Pageable pageable) {
        Page<Trip> tripPage = tripRepository.findAllByUsername(username, pageable);

        List<MyTripCardDto> tripCards = tripPage.getContent().stream()
                .map(trip -> {
                    MyTripCardDto dto = MyTripCardDto.fromEntity(trip);
                    dto.setLikesCount(favoriteRepository.countAllByTrip(trip));
                    return dto;
                })
                .toList();

        return new MyTripCardsContainerDto(tripCards,  tripPage.getNumber(), tripPage.getTotalPages());
    }

    @Cacheable(value = "trips", key = "#id")
    public TripDto findById(Long id) {
        return tripRepository.findById(id)
                .map(TripDto::fromEntity)
                .orElseThrow(() -> new TripNotFoundException(id.toString()));
    }

    @CacheEvict(value = {"tripPages", "userTrips", "trips"}, allEntries = true)
    public TripDto save(AddTripRequest request, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Trip trip = Trip.builder()
                .departurePoint(request.getDeparturePoint())
                .destination(request.getDestination())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .budget(request.getBudget())
                .maxFellows(request.getMaxFellows())
                .description(request.getDescription())
                .tags(request.getTags())
                .owner(owner)
                .build();

        return TripDto.fromEntity(tripRepository.save(trip));
    }

    @Caching(evict = {
            @CacheEvict(value = "trips", key = "#id"),
            @CacheEvict(value = "tripPages", allEntries = true),
            @CacheEvict(value = "userTrips", allEntries = true)
    })
    public TripDto update(Long id, UpdateTripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id.toString()));

        trip.setBudget(request.getBudget());
        trip.setMaxFellows(request.getMaxFellows());
        trip.setDescription(request.getDescription());

        return TripDto.fromEntity(tripRepository.save(trip));
    }

    @Caching(evict = {
            @CacheEvict(value = "trips", key = "#id"),
            @CacheEvict(value = "tripPages", allEntries = true),
            @CacheEvict(value = "userTrips", allEntries = true)
    })
    public void delete(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id.toString()));

        tripRepository.delete(trip);
    }
}
