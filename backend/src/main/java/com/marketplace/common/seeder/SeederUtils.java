package com.marketplace.common.seeder;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Base utility class for all seeders providing common helper methods
 */
@Component
public class SeederUtils {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public Faker getFaker() {
        return faker;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * Generate a random LocalDateTime between two dates
     */
    public LocalDateTime randomDateBetween(LocalDateTime start, LocalDateTime end) {
        long startEpoch = start.atZone(ZoneId.systemDefault()).toEpochSecond();
        long endEpoch = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long randomEpoch = startEpoch + (long) (random.nextDouble() * (endEpoch - startEpoch));
        return LocalDateTime.ofEpochSecond(randomEpoch, 0,
                ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now()));
    }

    /**
     * Generate a random past date within days
     */
    public LocalDateTime randomPastDate(int days) {
        return LocalDateTime.now().minusDays(random.nextInt(days));
    }

    /**
     * Generate a random future date within days
     */
    public LocalDateTime randomFutureDate(int days) {
        return LocalDateTime.now().plusDays(random.nextInt(days) + 1);
    }

    /**
     * Pick a random element from an array
     */
    public <T> T randomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }

    /**
     * Pick a random element from a list
     */
    public <T> T randomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Pick multiple random elements from a list
     */
    public <T> List<T> randomElements(List<T> list, int count) {
        List<T> shuffled = new ArrayList<>(list);
        java.util.Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    /**
     * Generate a random boolean with given probability (0.0 to 1.0)
     */
    public boolean randomBoolean(double probability) {
        return random.nextDouble() < probability;
    }

    /**
     * Generate a random integer between min and max (inclusive)
     */
    public int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Generate a random double between min and max
     */
    public double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Generate a random price
     */
    public double randomPrice(double min, double max) {
        double price = randomDouble(min, max);
        return Math.round(price * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Generate realistic car mileage based on year
     */
    public int generateMileage(int year) {
        int currentYear = LocalDateTime.now().getYear();
        int age = currentYear - year;
        int avgMilesPerYear = randomInt(8000, 15000);
        return age * avgMilesPerYear + randomInt(0, 5000);
    }

    /**
     * Generate a random phone number
     */
    public String randomPhoneNumber() {
        return String.format("+1%d%d%d%d%d%d%d%d%d%d",
                random.nextInt(10), random.nextInt(10), random.nextInt(10),
                random.nextInt(10), random.nextInt(10), random.nextInt(10),
                random.nextInt(10), random.nextInt(10), random.nextInt(10),
                random.nextInt(10));
    }

    /**
     * Generate a weighted random choice
     */
    public <T> T weightedRandom(List<T> items, List<Double> weights) {
        double totalWeight = weights.stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;
        for (int i = 0; i < items.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (randomValue <= cumulativeWeight) {
                return items.get(i);
            }
        }
        return items.get(items.size() - 1);
    }
}
