package com.marketplace.listing.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.listing.dto.ListingDTO;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.entity.ListingImage;
import com.marketplace.listing.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    public ListingDTO.ListingResponse createListing(ListingDTO.CreateListingRequest request, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate unique slug
        String baseSlug = com.marketplace.common.util.SlugUtil.generateSlug(request.getTitle());
        String slug = baseSlug;
        int attempt = 0;
        while (listingRepository.findBySlug(slug).isPresent()) {
            attempt++;
            slug = com.marketplace.common.util.SlugUtil.generateUniqueSlug(baseSlug, attempt);
        }

        Listing listing = Listing.builder()
                .title(request.getTitle())
                .slug(slug)
                .description(request.getDescription())
                .price(request.getPrice())
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .mileage(request.getMileage())
                .location(request.getLocation())
                .listingType(request.getListingType())
                .status(Constants.LISTING_STATUS_PENDING)
                .isFeatured(false)
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .color(request.getColor())
                .seatingCapacity(request.getSeatingCapacity())
                .features(request.getFeatures())
                .owner(owner)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ListingImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ListingImage image = ListingImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .listing(listing)
                        .build();
                images.add(image);
            }
            listing.setImages(images);
        }

        listing = listingRepository.save(listing);
        return convertToResponse(listing);
    }

    public ListingDTO.ListingResponse updateListing(Long listingId, ListingDTO.UpdateListingRequest request,
            Long userId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        if (!listing.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to update this listing");
        }

        if (request.getTitle() != null)
            listing.setTitle(request.getTitle());
        if (request.getDescription() != null)
            listing.setDescription(request.getDescription());
        if (request.getPrice() != null)
            listing.setPrice(request.getPrice());
        if (request.getMake() != null)
            listing.setMake(request.getMake());
        if (request.getModel() != null)
            listing.setModel(request.getModel());
        if (request.getYear() != null)
            listing.setYear(request.getYear());
        if (request.getMileage() != null)
            listing.setMileage(request.getMileage());
        if (request.getLocation() != null)
            listing.setLocation(request.getLocation());
        if (request.getFuelType() != null)
            listing.setFuelType(request.getFuelType());
        if (request.getTransmission() != null)
            listing.setTransmission(request.getTransmission());
        if (request.getColor() != null)
            listing.setColor(request.getColor());
        if (request.getSeatingCapacity() != null)
            listing.setSeatingCapacity(request.getSeatingCapacity());
        if (request.getFeatures() != null)
            listing.setFeatures(request.getFeatures());

        if (request.getImageUrls() != null) {
            listing.getImages().clear();
            List<ListingImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ListingImage image = ListingImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .listing(listing)
                        .build();
                images.add(image);
            }
            listing.setImages(images);
        }

        listing = listingRepository.save(listing);
        return convertToResponse(listing);
    }

    public void deleteListing(Long listingId, Long userId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        if (!listing.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized to delete this listing");
        }

        listingRepository.delete(listing);
    }

    public ListingDTO.ListingResponse getListingById(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        return convertToResponse(listing);
    }

    public Page<ListingDTO.ListingResponse> searchListings(ListingDTO.ListingSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "desc";

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String status = request.getStatus() != null ? request.getStatus() : Constants.LISTING_STATUS_APPROVED;

        Page<Listing> listings = listingRepository.searchListings(
                request.getMake(),
                request.getModel(),
                request.getMinYear(),
                request.getMaxYear(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getLocation(),
                request.getListingType(),
                status,
                pageable);

        return listings.map(this::convertToResponse);
    }

    public ListingDTO.ListingResponse getListingBySlug(String slug) {
        Listing listing = listingRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with slug: " + slug));
        return convertToResponse(listing);
    }

    public List<ListingDTO.ListingResponse> getOwnerListings(Long ownerId) {
        return listingRepository.findByOwnerId(ownerId, Pageable.unpaged())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void approveListing(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        listing.setStatus(Constants.LISTING_STATUS_APPROVED);
        listingRepository.save(listing);
    }

    public void rejectListing(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        listing.setStatus(Constants.LISTING_STATUS_REJECTED);
        listingRepository.save(listing);
    }

    public Page<ListingDTO.ListingResponse> getPendingListings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Listing> listings = listingRepository.findByStatus(Constants.LISTING_STATUS_PENDING, pageable);
        return listings.map(this::convertToResponse);
    }

    private ListingDTO.ListingResponse convertToResponse(Listing listing) {
        ListingDTO.OwnerInfo ownerInfo = ListingDTO.OwnerInfo.builder()
                .id(listing.getOwner().getId())
                .firstName(listing.getOwner().getFirstName())
                .lastName(listing.getOwner().getLastName())
                .email(listing.getOwner().getEmail())
                .phone(listing.getOwner().getPhone())
                .build();

        List<String> imageUrls = listing.getImages().stream()
                .map(ListingImage::getImageUrl)
                .collect(Collectors.toList());

        return ListingDTO.ListingResponse.builder()
                .id(listing.getId())
                .slug(listing.getSlug())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .make(listing.getMake())
                .model(listing.getModel())
                .year(listing.getYear())
                .mileage(listing.getMileage())
                .location(listing.getLocation())
                .listingType(listing.getListingType())
                .status(listing.getStatus())
                .isFeatured(listing.getIsFeatured())
                .fuelType(listing.getFuelType())
                .transmission(listing.getTransmission())
                .color(listing.getColor())
                .seatingCapacity(listing.getSeatingCapacity())
                .features(listing.getFeatures())
                .owner(ownerInfo)
                .images(imageUrls)
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .build();
    }
}
