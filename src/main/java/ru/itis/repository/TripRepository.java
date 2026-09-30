package ru.itis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.entity.Trip;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query(value = "select t from Trip t join fetch t.owner",
            countQuery = "select count(t) from Trip t")
    Page<Trip> findAll(Pageable pageable);

    @Query(value = "select t from Trip t join fetch t.owner where t.owner.username = :username",
            countQuery = "select count(t) from Trip t where t.owner.username = :username")
    Page<Trip> findAllByUsername(@Param("username") String username, Pageable pageable);

    @Query("select t from Trip t join fetch t.owner where t.id = :id")
    Optional<Trip> findById(@Param("id") Long id);
}
