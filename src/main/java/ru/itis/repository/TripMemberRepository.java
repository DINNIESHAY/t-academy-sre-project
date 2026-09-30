package ru.itis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.entity.TripMember;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {

    boolean existsByTripIdAndUserId(Long tripId, Long userId);

    @Query(value = "select m from TripMember m join fetch m.user join fetch m.trip join fetch m.trip.owner where m.trip.id = :tripId",
            countQuery = "select count(m) from TripMember m where m.trip.id = :tripId")
    Page<TripMember> findAllByTripId(@Param("tripId") Long tripId, Pageable pageable);

    @Query(value = "select m from TripMember m join fetch m.user join fetch m.trip join fetch m.trip.owner where m.user.username = :username",
            countQuery = "select count(m) from TripMember m where m.user.username = :username")
    Page<TripMember> findAllByUsername(@Param("username") String username, Pageable pageable);

    long countByTripId(Long tripId);
}