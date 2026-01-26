package com.afyaquik.pharmacy.utils;

import com.afyaquik.pharmacy.services.DrugInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredDrugsDeactivator {
    private final DrugInventoryService drugInventoryService;
    //cron to deactivate expired drugs
    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
//    @Scheduled(cron = "0 */2 * * * ?")
    public void deactivateExpiredDrugs() {
        log.info("Deactivating expired inventories");
        drugInventoryService.deactivateExpiredDrugs();
    }
}
