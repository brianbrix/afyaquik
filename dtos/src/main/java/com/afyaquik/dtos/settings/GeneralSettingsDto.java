package com.afyaquik.dtos.settings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeneralSettingsDto {
   private Long id;
   private String settingKey;
   private String settingValue;

}
