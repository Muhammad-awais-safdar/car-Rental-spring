package com.marketplace.common.seeder;

import com.marketplace.category.entity.Make;
import com.marketplace.category.entity.Model;
import com.marketplace.category.repository.MakeRepository;
import com.marketplace.category.repository.ModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CategorySeeder {

    @Autowired
    private MakeRepository makeRepository;

    @Autowired
    private ModelRepository modelRepository;

    private static final Map<String, List<String>> MAKES_AND_MODELS = new LinkedHashMap<>() {
        {
            put("Toyota",
                    Arrays.asList("Camry", "Corolla", "RAV4", "Highlander", "Prius", "4Runner", "Tacoma", "Tundra"));
            put("Honda", Arrays.asList("Civic", "Accord", "CR-V", "Pilot", "Odyssey", "Ridgeline", "HR-V"));
            put("Ford", Arrays.asList("F-150", "Mustang", "Explorer", "Escape", "Edge", "Bronco", "Ranger"));
            put("Chevrolet",
                    Arrays.asList("Silverado", "Equinox", "Malibu", "Tahoe", "Suburban", "Traverse", "Camaro"));
            put("BMW", Arrays.asList("3 Series", "5 Series", "X3", "X5", "X7", "7 Series", "M3", "M5"));
            put("Mercedes-Benz", Arrays.asList("C-Class", "E-Class", "S-Class", "GLC", "GLE", "GLS", "A-Class"));
            put("Audi", Arrays.asList("A4", "A6", "Q5", "Q7", "Q8", "A3", "A8", "e-tron"));
            put("Nissan", Arrays.asList("Altima", "Sentra", "Rogue", "Pathfinder", "Murano", "Frontier", "Titan"));
            put("Hyundai", Arrays.asList("Elantra", "Sonata", "Tucson", "Santa Fe", "Palisade", "Kona"));
            put("Kia", Arrays.asList("Forte", "Optima", "Sorento", "Sportage", "Telluride", "Soul", "Stinger"));
            put("Mazda", Arrays.asList("Mazda3", "Mazda6", "CX-5", "CX-9", "CX-30", "MX-5 Miata"));
            put("Volkswagen", Arrays.asList("Jetta", "Passat", "Tiguan", "Atlas", "Golf", "ID.4"));
            put("Subaru", Arrays.asList("Outback", "Forester", "Crosstrek", "Impreza", "Ascent", "WRX"));
            put("Lexus", Arrays.asList("ES", "RX", "NX", "GX", "LX", "IS", "LS"));
            put("Jeep", Arrays.asList("Wrangler", "Grand Cherokee", "Cherokee", "Compass", "Renegade", "Gladiator"));
            put("Ram", Arrays.asList("1500", "2500", "3500", "ProMaster"));
            put("GMC", Arrays.asList("Sierra", "Terrain", "Acadia", "Yukon", "Canyon"));
            put("Dodge", Arrays.asList("Charger", "Challenger", "Durango"));
            put("Tesla", Arrays.asList("Model 3", "Model Y", "Model S", "Model X"));
            put("Porsche", Arrays.asList("911", "Cayenne", "Macan", "Panamera", "Taycan"));
        }
    };

    public void seed() {
        if (makeRepository.count() > 5) {
            log.info("Categories already seeded. Skipping...");
            return;
        }

        log.info("Seeding categories (makes and models)...");
        int totalModels = 0;

        for (Map.Entry<String, List<String>> entry : MAKES_AND_MODELS.entrySet()) {
            String makeName = entry.getKey();
            List<String> modelNames = entry.getValue();

            // Create Make
            Make make = new Make();
            make.setName(makeName);
            make.setLogoUrl("/images/makes/" + makeName.toLowerCase().replace(" ", "-") + ".png");
            make = makeRepository.save(make);

            // Create Models for this Make
            for (String modelName : modelNames) {
                Model model = new Model();
                model.setName(modelName);
                model.setMake(make);
                modelRepository.save(model);
                totalModels++;
            }
        }

        log.info("✓ Created {} makes with {} models", MAKES_AND_MODELS.size(), totalModels);
    }
}
