package com.marketplace.common.seeder;

import com.marketplace.auth.entity.Role;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.RoleRepository;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.util.SlugUtil;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.rental.entity.Driver;
import com.marketplace.rental.entity.Rental;
import com.marketplace.rental.repository.DriverRepository;
import com.marketplace.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Checking database for seed data...");

        // Seed users if they don't exist
        if (userRepository.count() == 0) {
            seedUsers();
            // Seed drivers independently (check by email)
            seedDrivers();
        }

        // Seed listings if they don't exist
        if (listingRepository.count() == 0) {
            seedListings();
        }

        System.out.println("✅ Database seeding check completed!");
    }

    private void seedUsers() {
        // Create admin user
        if (userRepository.findByEmail("admin@carmarket.com").isEmpty()) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhone("+1234567890");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setEmail("admin@carmarket.com");

            // Get all roles
            Set<Role> adminRoles = new HashSet<>(roleRepository.findAll());
            admin.setRoles(adminRoles);
            admin.setIsActive(true);
            admin.setIsVerified(true);
            userRepository.save(admin);
            System.out.println("✓ Created admin user (email: admin@carmarket.com, password: password)");
        }

        // Create sample users
        for (int i = 1; i <= 5; i++) {
            String email = "user" + i + "@carmarket.com";
            if (userRepository.findByEmail(email).isEmpty()) {
                User user = new User();
                user.setFirstName("User");
                user.setLastName("" + i);
                user.setPhone("+123456789" + i);
                user.setPassword(passwordEncoder.encode("password"));
                user.setEmail(email);

                // Get customer roles
                Set<Role> userRoles = new HashSet<>();
                roleRepository.findByName("CUSTOMER").ifPresent(userRoles::add);
                roleRepository.findByName("SELLER").ifPresent(userRoles::add);
                roleRepository.findByName("BUYER").ifPresent(userRoles::add);
                user.setRoles(userRoles);
                user.setIsActive(true);
                user.setIsVerified(true);
                userRepository.save(user);
            }
        }
        System.out.println("✓ Created 5 sample users (email: user1-5@carmarket.com, password: password)");
    }

    private void seedDrivers() {
        // Create sample drivers
        String[] driverNames = {
                "John", "Michael", "David", "James", "Robert",
                "William", "Richard", "Thomas", "Charles", "Daniel"
        };

        for (int i = 1; i <= 10; i++) {
            String email = "driver" + i + "@carmarket.com";

            // Check if user already exists
            if (userRepository.findByEmail(email).isEmpty()) {
                // Create user first
                User driverUser = new User();
                driverUser.setFirstName(driverNames[i - 1]);
                driverUser.setLastName("Driver");
                driverUser.setPhone("+1234567" + String.format("%03d", i));
                driverUser.setPassword(passwordEncoder.encode("password"));
                driverUser.setEmail(email);

                // Assign DRIVER role
                Set<Role> driverRoles = new HashSet<>();
                roleRepository.findByName("DRIVER").ifPresent(driverRoles::add);
                driverUser.setRoles(driverRoles);
                driverUser.setIsActive(true);
                driverUser.setIsVerified(true);

                // Save user
                User savedUser = userRepository.save(driverUser);

                // Create driver profile linked to user
                Driver driver = Driver.builder()
                        .user(savedUser)
                        .licenseNumber("DL" + String.format("%08d", i))
                        .yearsExperience(2 + (i % 8)) // 2-10 years experience
                        .bio("Professional driver with " + (2 + (i % 8)) + " years of experience")
                        .hourlyRate(new java.math.BigDecimal("15.00"))
                        .dailyRate(new java.math.BigDecimal("100.00"))
                        .weeklyRate(new java.math.BigDecimal("600.00"))
                        .isAvailable(true)
                        .rating(new java.math.BigDecimal("4." + (5 + (i % 5)))) // 4.5-4.9 rating
                        .totalTrips(10 + (i * 5))
                        .languages("English, Spanish")
                        .vehicleTypes("Sedan, SUV, Truck")
                        .build();

                driverRepository.save(driver);
            }
        }
        System.out.println("✓ Created 10 sample drivers (email: driver1-10@carmarket.com, password: password)");
    }

    private void seedListings() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty())
            return;

        String[][] cars = {
                // Make, Model, Year, FuelType, Transmission, Color
                { "Toyota", "Camry", "2023", "Gasoline", "Automatic", "Silver" },
                { "Toyota", "Corolla", "2022", "Gasoline", "Automatic", "White" },
                { "Toyota", "RAV4", "2023", "Hybrid", "Automatic", "Blue" },
                { "Toyota", "Highlander", "2022", "Gasoline", "Automatic", "Black" },
                { "Toyota", "Prius", "2023", "Hybrid", "Automatic", "Red" },

                { "Honda", "Civic", "2023", "Gasoline", "Automatic", "Gray" },
                { "Honda", "Accord", "2022", "Gasoline", "Automatic", "White" },
                { "Honda", "CR-V", "2023", "Hybrid", "Automatic", "Blue" },
                { "Honda", "Pilot", "2022", "Gasoline", "Automatic", "Black" },
                { "Honda", "Odyssey", "2023", "Gasoline", "Automatic", "Silver" },

                { "Ford", "Mustang", "2023", "Gasoline", "Manual", "Red" },
                { "Ford", "F-150", "2022", "Gasoline", "Automatic", "Blue" },
                { "Ford", "Explorer", "2023", "Gasoline", "Automatic", "Black" },
                { "Ford", "Escape", "2022", "Hybrid", "Automatic", "White" },
                { "Ford", "Bronco", "2023", "Gasoline", "Automatic", "Orange" },

                { "Chevrolet", "Silverado", "2023", "Gasoline", "Automatic", "Black" },
                { "Chevrolet", "Malibu", "2022", "Gasoline", "Automatic", "Silver" },
                { "Chevrolet", "Equinox", "2023", "Gasoline", "Automatic", "White" },
                { "Chevrolet", "Tahoe", "2022", "Gasoline", "Automatic", "Gray" },
                { "Chevrolet", "Camaro", "2023", "Gasoline", "Manual", "Yellow" },

                { "BMW", "3 Series", "2023", "Gasoline", "Automatic", "Black" },
                { "BMW", "5 Series", "2022", "Gasoline", "Automatic", "White" },
                { "BMW", "X5", "2023", "Gasoline", "Automatic", "Blue" },
                { "BMW", "X3", "2022", "Gasoline", "Automatic", "Gray" },
                { "BMW", "7 Series", "2023", "Gasoline", "Automatic", "Black" },

                { "Mercedes-Benz", "C-Class", "2023", "Gasoline", "Automatic", "Silver" },
                { "Mercedes-Benz", "E-Class", "2022", "Gasoline", "Automatic", "Black" },
                { "Mercedes-Benz", "GLE", "2023", "Gasoline", "Automatic", "White" },
                { "Mercedes-Benz", "GLC", "2022", "Gasoline", "Automatic", "Blue" },
                { "Mercedes-Benz", "S-Class", "2023", "Gasoline", "Automatic", "Black" },

                { "Audi", "A4", "2023", "Gasoline", "Automatic", "Gray" },
                { "Audi", "A6", "2022", "Gasoline", "Automatic", "White" },
                { "Audi", "Q5", "2023", "Gasoline", "Automatic", "Black" },
                { "Audi", "Q7", "2022", "Gasoline", "Automatic", "Blue" },
                { "Audi", "A8", "2023", "Gasoline", "Automatic", "Silver" },

                { "Tesla", "Model 3", "2023", "Electric", "Automatic", "White" },
                { "Tesla", "Model Y", "2023", "Electric", "Automatic", "Blue" },
                { "Tesla", "Model S", "2022", "Electric", "Automatic", "Black" },
                { "Tesla", "Model X", "2023", "Electric", "Automatic", "Red" },

                { "Nissan", "Altima", "2023", "Gasoline", "Automatic", "Silver" },
                { "Nissan", "Rogue", "2022", "Gasoline", "Automatic", "White" },
                { "Nissan", "Pathfinder", "2023", "Gasoline", "Automatic", "Black" },
                { "Nissan", "Maxima", "2022", "Gasoline", "Automatic", "Red" },

                { "Mazda", "CX-5", "2023", "Gasoline", "Automatic", "Blue" },
                { "Mazda", "Mazda3", "2022", "Gasoline", "Automatic", "Red" },
                { "Mazda", "CX-9", "2023", "Gasoline", "Automatic", "White" },
                { "Mazda", "MX-5 Miata", "2022", "Gasoline", "Manual", "Red" },

                { "Hyundai", "Elantra", "2023", "Gasoline", "Automatic", "Silver" },
                { "Hyundai", "Tucson", "2022", "Gasoline", "Automatic", "Blue" },
                { "Hyundai", "Santa Fe", "2023", "Gasoline", "Automatic", "Black" },
                { "Hyundai", "Palisade", "2022", "Gasoline", "Automatic", "White" },

                { "Kia", "Forte", "2023", "Gasoline", "Automatic", "Red" },
                { "Kia", "Sportage", "2022", "Gasoline", "Automatic", "Gray" },
                { "Kia", "Sorento", "2023", "Gasoline", "Automatic", "Black" },
                { "Kia", "Telluride", "2022", "Gasoline", "Automatic", "White" },
        };

        String[] locations = {
                "New York, NY", "Los Angeles, CA", "Chicago, IL", "Houston, TX",
                "Phoenix, AZ", "Philadelphia, PA", "San Antonio, TX", "San Diego, CA",
                "Dallas, TX", "San Jose, CA", "Austin, TX", "Jacksonville, FL",
                "Fort Worth, TX", "Columbus, OH", "Charlotte, NC", "San Francisco, CA",
                "Indianapolis, IN", "Seattle, WA", "Denver, CO", "Boston, MA"
        };

        for (int i = 0; i < cars.length; i++) {
            String[] carData = cars[i];
            User owner = users.get(random.nextInt(users.size()));

            // Create listing
            String title = carData[2] + " " + carData[0] + " " + carData[1];
            String slug = generateUniqueSlug(title, i);

            Listing listing = Listing.builder()
                    .title(title)
                    .slug(slug)
                    .description(generateDescription(carData[0], carData[1], carData[2]))
                    .make(carData[0])
                    .model(carData[1])
                    .year(Integer.parseInt(carData[2]))
                    .mileage(generateMileage(carData[2]))
                    .location(locations[random.nextInt(locations.length)])
                    .fuelType(carData[3])
                    .transmission(carData[4])
                    .color(carData[5])
                    .seatingCapacity(random.nextInt(3) + 4) // 4-7 seats
                    .features("Air Conditioning, Bluetooth, Backup Camera, Cruise Control, Keyless Entry")
                    .owner(owner)
                    .status("APPROVED")
                    .isFeatured(random.nextDouble() < 0.2) // 20% featured
                    .build();

            // Determine listing type (60% SELL, 40% RENT)
            boolean isForRent = random.nextDouble() < 0.4;
            listing.setListingType(isForRent ? "RENT" : "SELL");
            listing.setPrice(generatePrice(carData[0], carData[1], isForRent));

            listing.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(90)));
            listing.setUpdatedAt(listing.getCreatedAt());

            listing = listingRepository.save(listing);

            // Create rental if listing type is RENT
            if (isForRent) {
                BigDecimal dailyRate = generateDailyRate(carData[0], carData[1]);
                Rental rental = Rental.builder()
                        .listing(listing)
                        .dailyRate(dailyRate)
                        .weeklyRate(dailyRate.multiply(new BigDecimal("6.5")))
                        .monthlyRate(dailyRate.multiply(new BigDecimal("25")))
                        .deposit(dailyRate.multiply(new BigDecimal("3")))
                        .build();
                rentalRepository.save(rental);
            }

            if ((i + 1) % 10 == 0) {
                System.out.println("✓ Created " + (i + 1) + " listings...");
            }
        }

        System.out.println("✓ Created " + cars.length + " listings total");
    }

    private String generateDescription(String make, String model, String year) {
        String[] templates = {
                "Stunning %s %s %s with premium amenities and advanced safety features. Perfect for both city driving and long road trips. Well-maintained with full service history.",
                "Beautiful %s %s %s with low mileage. Equipped with the latest technology and comfort features. One owner, garage kept, ready to drive home today.",
                "Immaculate %s %s %s available now. Fuel efficient, reliable, and loaded with features. Comprehensive warranty available. Don't miss this opportunity!",
                "Premium %s %s %s in excellent condition. Features include leather interior, navigation system, sunroof, and much more. Financing options available.",
                "Exceptional %s %s %s ready for a new owner. Smooth ride, powerful engine, and packed with modern conveniences. Clean title, no accidents."
        };

        String template = templates[random.nextInt(templates.length)];
        return String.format(template, year, make, model);
    }

    private int generateMileage(String year) {
        int carYear = Integer.parseInt(year);
        int currentYear = LocalDateTime.now().getYear();
        int age = currentYear - carYear;

        if (age == 0) {
            return random.nextInt(5000) + 100;
        } else if (age == 1) {
            return random.nextInt(15000) + 5000;
        } else {
            return random.nextInt(30000) + (age * 12000);
        }
    }

    private BigDecimal generatePrice(String make, String model, boolean isForRent) {
        // Base prices by make (luxury vs regular)
        int basePrice = 25000;
        if (make.equals("BMW") || make.equals("Mercedes-Benz") || make.equals("Audi") || make.equals("Tesla")) {
            basePrice = 55000;
        } else if (make.equals("Ford") || make.equals("Chevrolet")) {
            basePrice = 30000;
        }

        int finalPrice = basePrice + random.nextInt(15000);

        // If for rent, price is much lower (monthly equivalent)
        if (isForRent) {
            finalPrice = finalPrice / 15; // Approximate monthly rental
        }

        return new BigDecimal(finalPrice);
    }

    private BigDecimal generateDailyRate(String make, String model) {
        int baseRate = 50;
        if (make.equals("BMW") || make.equals("Mercedes-Benz") || make.equals("Audi") || make.equals("Tesla")) {
            baseRate = 150;
        } else if (make.equals("Ford") || make.equals("Chevrolet")) {
            baseRate = 70;
        }

        int rate = baseRate + random.nextInt(40);
        return new BigDecimal(rate);
    }

    private String generateUniqueSlug(String title, int index) {
        String baseSlug = SlugUtil.generateSlug(title);
        String slug = baseSlug;

        // Check if slug exists, if so append index
        if (listingRepository.findBySlug(slug).isPresent()) {
            slug = SlugUtil.generateUniqueSlug(baseSlug, index);
        }

        return slug;
    }
}
