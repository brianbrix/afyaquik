package com.afyaquik.patients.config;

import com.afyaquik.patients.entity.TriageItem;
import com.afyaquik.patients.repository.TriageItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TriageItemDataLoader implements CommandLineRunner {
    private final TriageItemRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;
        for (String name : List.of("Weight (kg)", "Height (cm)", "Temperature (C)", "Systolic BP (mmHg)",
                "Diastolic BP (mmHg)", "Pulse (beats/min)", "Respiratory rate (breaths/min)", "Oxygen saturation (%)")) {
            TriageItem item = new TriageItem();
            item.setName(name);
            repository.save(item);
        }
    }
}