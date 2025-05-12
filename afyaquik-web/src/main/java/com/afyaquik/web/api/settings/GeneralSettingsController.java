package com.afyaquik.web.api.settings;

import com.afyaquik.core.settings.services.GeneralSettingsService;
import com.afyaquik.dtos.settings.GeneralSettingsDto;
import lombok.RequiredArgsConstructor;
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
    public List<GeneralSettingsDto> createGeneralSettings(List<GeneralSettingsDto> generalSettingsDto) {
        return generalSettingsService.createGeneralSettingsMultiple(generalSettingsDto);
    }
    @PostMapping("/general/update")
    public List<GeneralSettingsDto> updateGeneralSettings(List<GeneralSettingsDto> generalSettingsDto) {
        return generalSettingsService.updateMultipleSettings(generalSettingsDto);
    }
    @PostMapping("/general/delete")
    public GeneralSettingsDto deleteGeneralSettings(String key) {
        return generalSettingsService.deleteGeneralSettingsByKey(key);
    }
    @GetMapping("/general/{key}")
    public GeneralSettingsDto getGeneralSettingsByKey(@PathVariable String key) {
        return generalSettingsService.getGeneralSettingsByKey(key);
    }
    @PostMapping("/general/{key}")
    public GeneralSettingsDto updateGeneralSettingsByKey(@PathVariable String key, @org.springframework.web.bind.annotation.RequestBody GeneralSettingsDto generalSettingsDto) {
        return generalSettingsService.updateGeneralSettingsByKey(key, generalSettingsDto);
    }

}
