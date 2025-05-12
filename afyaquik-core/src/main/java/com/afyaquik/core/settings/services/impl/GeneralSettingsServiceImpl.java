package com.afyaquik.core.settings.services.impl;

import com.afyaquik.core.repository.GeneralSettingsRepository;
import com.afyaquik.core.settings.entity.GeneralSettings;
import com.afyaquik.core.settings.services.GeneralSettingsService;
import com.afyaquik.dtos.settings.GeneralSettingsDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GeneralSettingsServiceImpl implements GeneralSettingsService {
    private final GeneralSettingsRepository generalSettingsRepository;
    @Override
    public List<GeneralSettingsDto> getGeneralSettings() {
       return generalSettingsRepository.findAll().stream().map(generalSettings -> GeneralSettingsDto.builder()
                .id(generalSettings.getId())
                .settingKey(generalSettings.getSettingKey())
                .settingValue(generalSettings.getSettingValue())
                .build()).toList();
    }

    @Override
    public GeneralSettingsDto updateGeneralSettingsByKey(String key, GeneralSettingsDto generalSettingsDto) {
        GeneralSettings generalSettings = generalSettingsRepository.findBySettingKey(key).orElseThrow(()-> new EntityNotFoundException("Setting not found for  key: "+key));
        generalSettings.setSettingValue(generalSettingsDto.getSettingValue());
        generalSettings = generalSettingsRepository.save(generalSettings);
        return GeneralSettingsDto.builder()
                .id(generalSettings.getId())
                .settingKey(generalSettings.getSettingKey())
                .settingValue(generalSettings.getSettingValue())
                .build();
    }

    @Override
    public GeneralSettingsDto createGeneralSettings(GeneralSettingsDto generalSettingsDto) {
        GeneralSettings  generalSettings = new GeneralSettings();
        generalSettings.setSettingKey(generalSettingsDto.getSettingKey());
        generalSettings.setSettingValue(generalSettingsDto.getSettingValue());
        generalSettings = generalSettingsRepository.save(generalSettings);
        return GeneralSettingsDto.builder()
                .id(generalSettings.getId())
                .settingKey(generalSettings.getSettingKey())
                .settingValue(generalSettings.getSettingValue())
                .build();
    }

    @Override
    public List<GeneralSettingsDto> createGeneralSettingsMultiple(List<GeneralSettingsDto> generalSettingsDtoList) {
        return generalSettingsDtoList.stream().map(this::createGeneralSettings).toList();
    }

    @Override
    public GeneralSettingsDto getGeneralSettingsByKey(String key) {
       return generalSettingsRepository.findBySettingKey(key).map(generalSettings -> GeneralSettingsDto.builder()
                .id(generalSettings.getId())
                .settingKey(generalSettings.getSettingKey())
                .settingValue(generalSettings.getSettingValue())
                .build()).orElseThrow(()-> new EntityNotFoundException("Setting not found for  key: "+key));
    }

    @Override
    public GeneralSettingsDto deleteGeneralSettingsByKey(String key) {
       return generalSettingsRepository.findBySettingKey(key).map(generalSettings -> {
            generalSettingsRepository.delete(generalSettings);
            return GeneralSettingsDto.builder()
                    .id(generalSettings.getId())
                    .settingKey(generalSettings.getSettingKey())
                    .settingValue(generalSettings.getSettingValue())
                    .build();
        }).orElseThrow(()-> new EntityNotFoundException("Setting not found for  key: "+key));
    }

    @Override
    public List<GeneralSettingsDto> updateMultipleSettings(List<GeneralSettingsDto> generalSettingsDtos) {
        return generalSettingsDtos.stream().map(generalSettingsDto -> updateGeneralSettingsByKey(generalSettingsDto.getSettingKey(), generalSettingsDto)).toList();
    }

}
