package com.afyaquik.web.api.settings;

import com.afyaquik.core.settings.services.GeneralSettingsService;
import com.afyaquik.dtos.settings.GeneralSettingsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class GeneralSettingsController {
    private final GeneralSettingsService generalSettingsService;

    @GetMapping("/general")
    public List<GeneralSettingsDto> getGeneralSettings() {
        return generalSettingsService.getGeneralSettings();
    }

    @PostMapping("/general")
    public ResponseEntity<GeneralSettingsDto> createGeneralSettings(GeneralSettingsDto generalSettingsDto) {
        return ResponseEntity.ok(generalSettingsService.createGeneralSettings(generalSettingsDto));
    }
    @PutMapping("/general/update/{id}")
    public ResponseEntity<GeneralSettingsDto> updateGeneralSettings(@PathVariable Long id, @RequestBody GeneralSettingsDto generalSettingsDto) {
        return ResponseEntity.ok(generalSettingsService.updateGeneralSettings(id, generalSettingsDto));
    }

    @DeleteMapping("/general/{id}")
    public ResponseEntity<?> deleteGeneralSettings(@PathVariable Long  id) {
         generalSettingsService.deleteGeneralSettings(id);
         return ResponseEntity.ok().build();
    }
    @GetMapping("/general/{id}")
    public ResponseEntity<GeneralSettingsDto> getGeneralSettings(@PathVariable Long id) {
        return ResponseEntity.ok(generalSettingsService.getGeneralSettings(id));
    }
    @PostMapping("/general/{key}")
    public GeneralSettingsDto updateGeneralSettingsByKey(@PathVariable String key, @org.springframework.web.bind.annotation.RequestBody GeneralSettingsDto generalSettingsDto) {
        return generalSettingsService.updateGeneralSettingsByKey(key, generalSettingsDto);
    }


}
