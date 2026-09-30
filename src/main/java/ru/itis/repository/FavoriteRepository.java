package ru.itis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.entity.Favorite;
import ru.itis.entity.Trip;
import ru.itis.entity.User;

import java.util.Set;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("select f.trip.id from Favorite f where f.user.username = :username")
    Set<Long> findAllByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    void deleteByUserAndTrip(User user, Trip trip);

    Long countAllByTrip(Trip trip);

    @Query("select f.trip from Favorite f where f.user.username = :username")
    Page<Trip> findAllByUsername(String username, Pageable pageable);
}
