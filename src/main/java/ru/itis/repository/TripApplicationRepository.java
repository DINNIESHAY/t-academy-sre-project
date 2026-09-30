package ru.itis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.entity.TripApplication;

import java.util.Optional;

@Repository
public interface TripApplicationRepository extends JpaRepository<TripApplication, Long> {

    boolean existsByApplicantIdAndTripId(Long applicantId, Long tripId);

    @Query("select a from TripApplication a join fetch a.applicant join fetch a.trip join fetch a.trip.owner where a.id = :id")
    Optional<TripApplication> findByIdWithDetails(@Param("id") Long id);

    @Query("select a from TripApplication a where a.trip.owner.username = :username")
    Page<TripApplication> findAllByTripOwner(@Param("username") String username, Pageable pageable);
}