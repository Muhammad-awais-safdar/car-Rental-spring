package com.marketplace.common.seeder;

import com.marketplace.common.util.SlugUtil;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlugSeeder implements CommandLineRunner {

    private final ListingRepository listingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting slug generation for existing listings...");

        // Find all listings without slugs
        List<Listing> listingsWithoutSlugs = listingRepository.findAll().stream()
                .filter(listing -> listing.getSlug() == null || listing.getSlug().isEmpty())
                .toList();

        if (listingsWithoutSlugs.isEmpty()) {
            log.info("All listings already have slugs. Skipping slug generation.");
            return;
        }

        log.info("Found {} listings without slugs. Generating...", listingsWithoutSlugs.size());

        int updated = 0;
        for (Listing listing : listingsWithoutSlugs) {
            try {
                // Generate base slug from title
                String baseSlug = SlugUtil.generateSlug(listing.getTitle());
                String slug = baseSlug;
                int attempt = 0;

                // Ensure uniqueness
                while (listingRepository.findBySlug(slug).isPresent()) {
                    attempt++;
                    slug = SlugUtil.generateUniqueSlug(baseSlug, attempt);
                }

                // Update listing with slug
                listing.setSlug(slug);
                listingRepository.save(listing);
                updated++;

                log.debug("Generated slug '{}' for listing: {}", slug, listing.getTitle());
            } catch (Exception e) {
                log.error("Failed to generate slug for listing ID {}: {}", listing.getId(), e.getMessage());
            }
        }

        log.info("Successfully generated slugs for {} listings", updated);
    }
}
