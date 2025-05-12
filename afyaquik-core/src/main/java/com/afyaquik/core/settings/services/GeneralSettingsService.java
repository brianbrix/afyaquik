package com.afyaquik.core.settings.services;

import com.afyaquik.core.settings.entity.GeneralSettings;
import com.afyaquik.dtos.settings.GeneralSettingsDto;

import java.util.List;

public interface GeneralSettingsService {
    List<GeneralSettingsDto> getGeneralSettings();
    GeneralSettingsDto updateGeneralSettingsByKey(String key, GeneralSettingsDto generalSettingsDto);
    GeneralSettingsDto createGeneralSettings(GeneralSettingsDto generalSettingsDto);
    List<GeneralSettingsDto> createGeneralSettingsMultiple(List<GeneralSettingsDto> generalSettingsDtoList);
    GeneralSettingsDto getGeneralSettingsByKey(String key);
    GeneralSettingsDto deleteGeneralSettingsByKey(String key);
    List<GeneralSettingsDto> updateMultipleSettings(List<GeneralSettingsDto> generalSettingsDtos);
}
