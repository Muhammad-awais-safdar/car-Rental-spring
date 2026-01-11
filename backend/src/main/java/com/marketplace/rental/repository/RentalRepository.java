package com.marketplace.rental.repository;

import com.marketplace.rental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByListingId(Long listingId);

    boolean existsByListingId(Long listingId);

    @Query("SELECT r FROM Rental r JOIN r.listing l WHERE l.owner.id = :ownerId")
    java.util.List<Rental> findByOwnerId(@Param("ownerId") Long ownerId);
}
