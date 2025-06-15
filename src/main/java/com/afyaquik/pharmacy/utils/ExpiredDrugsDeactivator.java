package com.afyaquik.pharmacy.utils;

import com.afyaquik.pharmacy.services.DrugInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredDrugsDeactivator {
    private final DrugInventoryService drugInventoryService;
    //cron to deactivate expired drugs
    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
    public void deactivateExpiredDrugs() {
        drugInventoryService.deactivateExpiredDrugs();
    }
}
