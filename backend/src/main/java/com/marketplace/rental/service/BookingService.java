package com.marketplace.rental.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.rental.dto.BookingDTO;
import com.marketplace.rental.entity.Booking;
import com.marketplace.rental.entity.Rental;
import com.marketplace.rental.repository.BookingRepository;
import com.marketplace.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    public BookingDTO.BookingResponse createBooking(BookingDTO.CreateBookingRequest request, Long userId) {
        // Validate rental exists
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }

        // Check availability
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.getRentalId(), request.getStartDate(), request.getEndDate());

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Rental is not available for selected dates");
        }

        // Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(rental, request.getStartDate(), request.getEndDate());

        // Create booking with driver and location details
        Booking booking = Booking.builder()
                .rental(rental)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalAmount(totalAmount)
                .status("REQUESTED")
                .driverName(request.getDriverName())
                .driverEmail(request.getDriverEmail())
                .driverPhone(request.getDriverPhone())
                .driverLicense(request.getDriverLicense())
                .needsDriver(request.getNeedsDriver() != null ? request.getNeedsDriver() : false)
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .notes(request.getNotes())
                .build();

        booking = bookingRepository.save(booking);
        return convertToResponse(booking);
    }

    public BookingDTO.BookingResponse confirmBooking(Long id, Long adminId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!"REQUESTED".equals(booking.getStatus())) {
            throw new BadRequestException("Only requested bookings can be confirmed");
        }

        booking.setStatus("CONFIRMED");
        booking = bookingRepository.save(booking);

        return convertToResponse(booking);
    }

    public BookingDTO.BookingResponse cancelBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Check if user owns the booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to cancel this booking");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            throw new BadRequestException("Cannot cancel this booking");
        }

        booking.setStatus("CANCELLED");
        booking = bookingRepository.save(booking);

        return convertToResponse(booking);
    }

    public BookingDTO.BookingResponse getBookingById(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Check if user owns the booking or owns the rental
        boolean isOwner = booking.getUser().getId().equals(userId);
        boolean isRentalOwner = booking.getRental().getListing().getOwner().getId().equals(userId);

        if (!isOwner && !isRentalOwner) {
            throw new BadRequestException("Unauthorized to view this booking");
        }

        return convertToResponse(booking);
    }

    public Page<BookingDTO.BookingListResponse> getUserBookings(Long userId, Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findByUserId(userId, pageable);
        return bookings.map(this::convertToListResponse);
    }

    public Page<BookingDTO.BookingListResponse> getUserBookingsByStatus(Long userId, String status, Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findByUserIdAndStatus(userId, status, pageable);
        return bookings.map(this::convertToListResponse);
    }

    public List<BookingDTO.BookingResponse> getRentalBookings(Long rentalId, Long userId) {
        // Verify user owns the rental
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        if (!rental.getListing().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to view bookings for this rental");
        }

        List<Booking> bookings = bookingRepository.findByRentalId(rentalId);
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalAmount(Rental rental, LocalDate startDate, LocalDate endDate) {
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

    private BookingDTO.BookingResponse convertToResponse(Booking booking) {
        String listingImage = booking.getRental().getListing().getImages().isEmpty()
                ? null
                : booking.getRental().getListing().getImages().get(0).getImageUrl();

        return BookingDTO.BookingResponse.builder()
                .id(booking.getId())
                .rentalId(booking.getRental().getId())
                .listingTitle(booking.getRental().getListing().getTitle())
                .listingImage(listingImage)
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .driverName(booking.getDriverName())
                .driverEmail(booking.getDriverEmail())
                .driverPhone(booking.getDriverPhone())
                .driverLicense(booking.getDriverLicense())
                .needsDriver(booking.getNeedsDriver())
                .pickupLocation(booking.getPickupLocation())
                .dropoffLocation(booking.getDropoffLocation())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    private BookingDTO.BookingListResponse convertToListResponse(Booking booking) {
        String listingImage = booking.getRental().getListing().getImages().isEmpty()
                ? null
                : booking.getRental().getListing().getImages().get(0).getImageUrl();

        return BookingDTO.BookingListResponse.builder()
                .id(booking.getId())
                .listingTitle(booking.getRental().getListing().getTitle())
                .listingImage(listingImage)
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
