package com.marketplace.ride.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.ride.dto.RideDTO;
import com.marketplace.ride.service.BidService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class BidController {

    @Autowired
    private BidService bidService;

    /**
     * POST /api/rides/{rideId}/bids
     * Driver places a bid on a ride.
     */
    @PostMapping("/{rideId}/bids")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.BidResponse>> placeBid(
            @PathVariable Long rideId,
            @Valid @RequestBody RideDTO.CreateBidRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.BidResponse response = bidService.placeBid(rideId, request, userId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, Constants.MSG_BID_PLACED, response),
                HttpStatus.CREATED);
    }

    /**
     * DELETE /api/rides/bids/{bidId}
     * Driver withdraws their bid.
     */
    @DeleteMapping("/bids/{bidId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.BidResponse>> withdrawBid(
            @PathVariable Long bidId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.BidResponse response = bidService.withdrawBid(bidId, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Bid withdrawn successfully", response));
    }

    /**
     * GET /api/rides/{rideId}/bids
     * Passenger views all bids on their ride.
     */
    @GetMapping("/{rideId}/bids")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<RideDTO.BidResponse>>> getBidsForRide(
            @PathVariable Long rideId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        List<RideDTO.BidResponse> response = bidService.getBidsForRide(rideId, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response));
    }

    /**
     * GET /api/rides/bids/my
     * Driver views all their bids.
     */
    @GetMapping("/bids/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<RideDTO.BidResponse>>> getMyBids(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        List<RideDTO.BidResponse> response = bidService.getMyBids(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response));
    }
}
