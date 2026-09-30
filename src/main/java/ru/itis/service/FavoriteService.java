package ru.itis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.TripCardDto;
import ru.itis.dto.TripCardsContainerDto;
import ru.itis.entity.Favorite;
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
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository,  UserRepository userRepository, TripRepository tripRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository =  userRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    @CacheEvict(value = {"tripPages", "userTrips", "trips", "favoriteTrips"}, allEntries = true)
    public void toggleFavorite(Long tripId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId.toString()));

        Set<Long> favoriteIds = favoriteRepository.findAllByUsername(username);

        if (favoriteIds.contains(tripId)) {
            favoriteRepository.deleteByUserAndTrip(user, trip);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setTrip(trip);
            favoriteRepository.save(favorite);
        }
    }

    @Cacheable(value = "favoriteTrips", key = "#username + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public TripCardsContainerDto findFavoriteTrips(String username, Pageable pageable) {
        Page<Trip> favoritesPage = favoriteRepository.findAllByUsername(username, pageable);

        List<TripCardDto> tripCards = favoritesPage.getContent().stream()
                .map(trip -> {
                    TripCardDto dto = TripCardDto.fromEntity(trip);
                    dto.setIsFavorite(true);
                    return dto;
                })
                .toList();

        return new TripCardsContainerDto(tripCards, favoritesPage.getNumber(), favoritesPage.getTotalPages());
    }
}
