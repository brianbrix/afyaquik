package com.afyaquik.dtos.patient;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageReportDto {
    private Long id;
    private Long patientVisitId;
    private String bpReport;
    private String temperatureReport;
    private String pulseReport;
    private String weightReport;
    private String heightReport;
    private String bmiReport;
    private String respiratoryRateReport;
    private String oxygenSaturationReport;
    private String bloodSugarReport;
    private String complaintSummaryReport;
}
