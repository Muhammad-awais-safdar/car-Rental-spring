package com.marketplace.rental.service;

import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.entity.ListingImage;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.rental.dto.RentalDTO;
import com.marketplace.rental.entity.Booking;
import com.marketplace.rental.entity.Rental;
import com.marketplace.rental.repository.BookingRepository;
import com.marketplace.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public RentalDTO.RentalResponse createRental(RentalDTO.CreateRentalRequest request, Long userId) {
        // Check if listing exists
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        // Check if user owns the listing
        if (!listing.getOwner().getId().equals(userId)) {
            throw new BadRequestException("You can only create rentals for your own listings");
        }

        // Check if rental already exists for this listing
        if (rentalRepository.existsByListingId(request.getListingId())) {
            throw new BadRequestException("Rental already exists for this listing");
        }

        // Create rental
        Rental rental = Rental.builder()
                .listing(listing)
                .dailyRate(request.getDailyRate())
                .weeklyRate(request.getWeeklyRate())
                .monthlyRate(request.getMonthlyRate())
                .deposit(request.getDeposit())
                .build();

        rental = rentalRepository.save(rental);
        return convertToResponse(rental);
    }

    public RentalDTO.RentalResponse updateRental(Long id, RentalDTO.UpdateRentalRequest request, Long userId) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        // Check ownership
        if (!rental.getListing().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to update this rental");
        }

        // Update fields
        if (request.getDailyRate() != null)
            rental.setDailyRate(request.getDailyRate());
        if (request.getWeeklyRate() != null)
            rental.setWeeklyRate(request.getWeeklyRate());
        if (request.getMonthlyRate() != null)
            rental.setMonthlyRate(request.getMonthlyRate());
        if (request.getDeposit() != null)
            rental.setDeposit(request.getDeposit());

        rental = rentalRepository.save(rental);
        return convertToResponse(rental);
    }

    public void deleteRental(Long id, Long userId) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        // Check ownership
        if (!rental.getListing().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to delete this rental");
        }

        // Check for active bookings
        List<Booking> activeBookings = bookingRepository.findConflictingBookings(
                id, LocalDate.now(), LocalDate.now().plusYears(1));

        if (!activeBookings.isEmpty()) {
            throw new BadRequestException("Cannot delete rental with active bookings");
        }

        rentalRepository.delete(rental);
    }

    public RentalDTO.RentalResponse getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        return convertToResponse(rental);
    }

    public RentalDTO.RentalResponse getRentalByListingId(Long listingId) {
        Rental rental = rentalRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found for this listing"));
        return convertToResponse(rental);
    }

    public List<RentalDTO.RentalResponse> getUserRentals(Long userId) {
        List<Rental> rentals = rentalRepository.findByOwnerId(userId);
        return rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public RentalDTO.AvailabilityResponse checkAvailability(Long id, RentalDTO.AvailabilityRequest request) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Check for conflicting bookings
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                id, request.getStartDate(), request.getEndDate());

        boolean available = conflicts.isEmpty();
        String message = available ? "Available for booking" : "Not available for selected dates";

        // Calculate estimated cost
        BigDecimal estimatedCost = calculateCost(rental, request.getStartDate(), request.getEndDate());

        return RentalDTO.AvailabilityResponse.builder()
                .available(available)
                .message(message)
                .estimatedCost(estimatedCost)
                .build();
    }

    private BigDecimal calculateCost(Rental rental, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Use monthly rate if >= 30 days
        if (days >= 30 && rental.getMonthlyRate() != null) {
            long months = days / 30;
            long remainingDays = days % 30;
            return rental.getMonthlyRate().multiply(BigDecimal.valueOf(months))
                    .add(rental.getDailyRate().multiply(BigDecimal.valueOf(remainingDays)));
        }

        // Use weekly rate if >= 7 days
        if (days >= 7 && rental.getWeeklyRate() != null) {
            long weeks = days / 7;
            long remainingDays = days % 7;
            return rental.getWeeklyRate().multiply(BigDecimal.valueOf(weeks))
                    .add(rental.getDailyRate().multiply(BigDecimal.valueOf(remainingDays)));
        }

        // Use daily rate
        return rental.getDailyRate().multiply(BigDecimal.valueOf(days));
    }

    private RentalDTO.RentalResponse convertToResponse(Rental rental) {
        return RentalDTO.RentalResponse.builder()
                .id(rental.getId())
                .listingId(rental.getListing().getId())
                .listingTitle(rental.getListing().getTitle())
                .dailyRate(rental.getDailyRate())
                .weeklyRate(rental.getWeeklyRate())
                .monthlyRate(rental.getMonthlyRate())
                .deposit(rental.getDeposit())
                .createdAt(rental.getCreatedAt())
                .updatedAt(rental.getUpdatedAt())
                .build();
    }
}
