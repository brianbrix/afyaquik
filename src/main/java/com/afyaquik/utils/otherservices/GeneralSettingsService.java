package com.afyaquik.utils.otherservices;

import com.afyaquik.dtos.settings.GeneralSettingsDto;

import java.util.List;

public interface GeneralSettingsService {
    List<GeneralSettingsDto> getGeneralSettings();
    GeneralSettingsDto updateGeneralSettingsByKey(String key, GeneralSettingsDto generalSettingsDto);
    GeneralSettingsDto updateGeneralSettings(Long id, GeneralSettingsDto generalSettingsDto);
    GeneralSettingsDto createGeneralSettings(GeneralSettingsDto generalSettingsDto);
    GeneralSettingsDto getGeneralSettingsByKey(String key);
    GeneralSettingsDto getGeneralSettings(Long id);
    void deleteGeneralSettings(Long id);
    GeneralSettingsDto deleteGeneralSettingsByKey(String key);
    List<GeneralSettingsDto> updateMultipleSettings(List<GeneralSettingsDto> generalSettingsDtos);
}
