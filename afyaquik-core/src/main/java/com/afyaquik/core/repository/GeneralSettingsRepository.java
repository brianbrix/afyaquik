package com.afyaquik.core.repository;

import com.afyaquik.core.settings.entity.GeneralSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralSettingsRepository extends JpaRepository<GeneralSettings, Long>{
    Optional<GeneralSettings> findBySettingKey(String settingKey);
}
