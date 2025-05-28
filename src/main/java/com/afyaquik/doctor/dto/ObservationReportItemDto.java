package com.afyaquik.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class ObservationReportItemDto {
    private Long id;
    @NotBlank(message = "Item id is required")
    private Long itemId;
    private String itemName;
    private Long reportId;
    @NotBlank(message = "Observation value is required")
    @Length(max = 20000, message = "Observation value must be less than 20000 characters")
    private String value;
    @Length(max = 3000, message = "Comment must be less than 255 characters")
    private String comment;

}
