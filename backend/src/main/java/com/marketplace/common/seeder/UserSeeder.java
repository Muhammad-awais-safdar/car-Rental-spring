package com.marketplace.common.seeder;

import com.marketplace.auth.entity.Role;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.RoleRepository;
import com.marketplace.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class UserSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SeederUtils utils;

    public void seed() {
        if (userRepository.count() > 20) {
            log.info("Users already seeded. Skipping additional user seeding...");
            return;
        }

        log.info("Seeding additional users...");
        List<User> users = new ArrayList<>();

        // Get roles from database
        Optional<Role> adminRole = roleRepository.findByName("ADMIN");
        Optional<Role> sellerRole = roleRepository.findByName("SELLER");
        Optional<Role> buyerRole = roleRepository.findByName("BUYER");
        Optional<Role> renterRole = roleRepository.findByName("RENTER");

        // Create additional Seller users
        if (sellerRole.isPresent()) {
            users.addAll(createSellers(10, sellerRole.get()));
        }

        // Create additional Buyer users
        if (buyerRole.isPresent()) {
            users.addAll(createBuyers(20, buyerRole.get()));
        }

        // Create Renter users
        if (renterRole.isPresent()) {
            users.addAll(createRenters(10, renterRole.get()));
        }

        userRepository.saveAll(users);
        log.info("✓ Created {} additional users", users.size());
    }

    private List<User> createSellers(int count, Role sellerRole) {
        List<User> sellers = new ArrayList<>();
        String[] verificationLevels = { "NONE", "BASIC", "STANDARD", "PREMIUM" };

        for (int i = 1; i <= count; i++) {
            boolean isVerified = utils.randomBoolean(0.7); // 70% verified
            String verLevel = isVerified ? utils.randomElement(verificationLevels) : "NONE";

            User seller = new User();
            seller.setFirstName(utils.getFaker().name().firstName());
            seller.setLastName(utils.getFaker().name().lastName());
            seller.setEmail("seller" + (i + 10) + "@example.com");
            seller.setPhone(utils.randomPhoneNumber());
            seller.setPassword(passwordEncoder.encode("password"));
            seller.setRoles(new HashSet<>(Collections.singletonList(sellerRole)));
            seller.setIsBlocked(utils.randomBoolean(0.05)); // 5% blocked
            seller.setIsActive(true);
            seller.setIsVerified(isVerified);
            seller.setVerificationLevel(verLevel);
            seller.setVerifiedAt(isVerified ? utils.randomPastDate(180) : null);
            seller.setCreatedAt(utils.randomPastDate(365));

            sellers.add(seller);
        }
        return sellers;
    }

    private List<User> createBuyers(int count, Role buyerRole) {
        List<User> buyers = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            boolean isVerified = utils.randomBoolean(0.5); // 50% verified

            User buyer = new User();
            buyer.setFirstName(utils.getFaker().name().firstName());
            buyer.setLastName(utils.getFaker().name().lastName());
            buyer.setEmail("buyer" + (i + 10) + "@example.com");
            buyer.setPhone(utils.randomPhoneNumber());
            buyer.setPassword(passwordEncoder.encode("password"));
            buyer.setRoles(new HashSet<>(Collections.singletonList(buyerRole)));
            buyer.setIsBlocked(utils.randomBoolean(0.02)); // 2% blocked
            buyer.setIsActive(true);
            buyer.setIsVerified(isVerified);
            buyer.setVerificationLevel(isVerified ? "BASIC" : "NONE");
            buyer.setVerifiedAt(isVerified ? utils.randomPastDate(180) : null);
            buyer.setCreatedAt(utils.randomPastDate(365));

            buyers.add(buyer);
        }
        return buyers;
    }

    private List<User> createRenters(int count, Role renterRole) {
        List<User> renters = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            boolean isVerified = utils.randomBoolean(0.6); // 60% verified

            User renter = new User();
            renter.setFirstName(utils.getFaker().name().firstName());
            renter.setLastName(utils.getFaker().name().lastName());
            renter.setEmail("renter" + i + "@example.com");
            renter.setPhone(utils.randomPhoneNumber());
            renter.setPassword(passwordEncoder.encode("password"));
            renter.setRoles(new HashSet<>(Collections.singletonList(renterRole)));
            renter.setIsBlocked(false);
            renter.setIsActive(true);
            renter.setIsVerified(isVerified);
            renter.setVerificationLevel(isVerified ? "STANDARD" : "NONE");
            renter.setVerifiedAt(isVerified ? utils.randomPastDate(90) : null);
            renter.setCreatedAt(utils.randomPastDate(180));

            renters.add(renter);
        }
        return renters;
    }
}
