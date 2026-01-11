package com.marketplace.rental.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.rental.dto.BookingDTO;
import com.marketplace.rental.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

        @Autowired
        private BookingService bookingService;

        @PostMapping
        public ResponseEntity<ApiResponse<BookingDTO.BookingResponse>> createBooking(
                        @Valid @RequestBody BookingDTO.CreateBookingRequest request,
                        Authentication authentication) {

                Long userId = authentication != null ? Long.parseLong(authentication.getPrincipal().toString()) : null;
                BookingDTO.BookingResponse response = bookingService.createBooking(request, userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_CREATED, Constants.MSG_BOOKING_CREATED, response),
                                HttpStatus.CREATED);
        }

        @PutMapping("/{id}/confirm")
        @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
        public ResponseEntity<ApiResponse<BookingDTO.BookingResponse>> confirmBooking(
                        @PathVariable Long id,
                        Authentication authentication) {

                Long adminId = Long.parseLong(authentication.getPrincipal().toString());
                BookingDTO.BookingResponse response = bookingService.confirmBooking(id, adminId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_BOOKING_CONFIRMED,
                                                response),
                                HttpStatus.OK);
        }

        @PutMapping("/{id}/cancel")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<BookingDTO.BookingResponse>> cancelBooking(
                        @PathVariable Long id,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                BookingDTO.BookingResponse response = bookingService.cancelBooking(id, userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_BOOKING_CANCELLED,
                                                response),
                                HttpStatus.OK);
        }

        @GetMapping("/{id}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<BookingDTO.BookingResponse>> getBookingById(
                        @PathVariable Long id,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                BookingDTO.BookingResponse response = bookingService.getBookingById(id, userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                                HttpStatus.OK);
        }

        @GetMapping("/my")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<Page<BookingDTO.BookingListResponse>>> getUserBookings(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortOrder,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<BookingDTO.BookingListResponse> response = bookingService.getUserBookings(userId, pageable);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                                HttpStatus.OK);
        }

        @GetMapping("/my/status/{status}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<Page<BookingDTO.BookingListResponse>>> getUserBookingsByStatus(
                        @PathVariable String status,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

                Page<BookingDTO.BookingListResponse> response = bookingService.getUserBookingsByStatus(userId, status,
                                pageable);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                                HttpStatus.OK);
        }

        @GetMapping("/rental/{rentalId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<List<BookingDTO.BookingResponse>>> getRentalBookings(
                        @PathVariable Long rentalId,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                List<BookingDTO.BookingResponse> response = bookingService.getRentalBookings(rentalId, userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                                HttpStatus.OK);
        }
}
