package com.afyaquik.dtos.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralSettingsDto {
   private Long id;
   private String settingKey;
   private String settingValue;

}
