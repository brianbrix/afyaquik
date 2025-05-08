package com.afyaquik.patients.services.impl;

import com.afyaquik.dtos.patient.TriageReportDto;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.entity.TriageReport;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.repository.TriageReportRepository;
import com.afyaquik.patients.services.TriageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TriageServiceImpl implements TriageService {
    private final PatientVisitRepo patientVisitRepository;
    private final TriageReportRepository triageReportRepository;
    @Override
    public TriageReportDto createTriageReport(TriageReportDto triageReportDto) {
        PatientVisit patientVisit = patientVisitRepository.findById(triageReportDto.getPatientVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        TriageReport triageReport = TriageReport.builder()
                .patientVisit(patientVisit)
                .bpReport(triageReportDto.getBpReport())
                .temperatureReport(triageReportDto.getTemperatureReport())
                .pulseReport(triageReportDto.getPulseReport())
                .weightReport(triageReportDto.getWeightReport())
                .heightReport(triageReportDto.getHeightReport())
                .bmiReport(triageReportDto.getBmiReport())
                .respiratoryRateReport(triageReportDto.getRespiratoryRateReport())
                .oxygenSaturationReport(triageReportDto.getOxygenSaturationReport())
                .bloodSugarReport(triageReportDto.getBloodSugarReport())
                .complaintSummaryReport(triageReportDto.getComplaintSummaryReport())
                .build();
        patientVisit.setTriageReport(triageReport);
        patientVisitRepository.save(patientVisit);
        return TriageReportDto.builder()
                .id(triageReport.getId())
                .patientVisitId(triageReport.getPatientVisit().getId())
                .bpReport(triageReport.getBpReport())
                .temperatureReport(triageReport.getTemperatureReport())
                .pulseReport(triageReport.getPulseReport())
                .weightReport(triageReport.getWeightReport())
                .heightReport(triageReport.getHeightReport())
                .bmiReport(triageReport.getBmiReport())
                .respiratoryRateReport(triageReport.getRespiratoryRateReport())
                .oxygenSaturationReport(triageReport.getOxygenSaturationReport())
                .bloodSugarReport(triageReport.getBloodSugarReport())
                .complaintSummaryReport(triageReport.getComplaintSummaryReport())
                .build();
    }

    @Override
    public TriageReportDto updateTriageReport(TriageReportDto triageReportDto) {
        TriageReport  triageReport = triageReportRepository.findById(triageReportDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Triage report not found"));
        triageReport.setBpReport(triageReportDto.getBpReport());
        triageReport.setTemperatureReport(triageReportDto.getTemperatureReport());
        triageReport.setPulseReport(triageReportDto.getPulseReport());
        triageReport.setWeightReport(triageReportDto.getWeightReport());
        triageReport.setHeightReport(triageReportDto.getHeightReport());
        triageReport.setBmiReport(triageReportDto.getBmiReport());
        triageReport.setRespiratoryRateReport(triageReportDto.getRespiratoryRateReport());
        triageReport.setOxygenSaturationReport(triageReportDto.getOxygenSaturationReport());
        triageReport.setBloodSugarReport(triageReportDto.getBloodSugarReport());
        triageReport.setComplaintSummaryReport(triageReportDto.getComplaintSummaryReport());
        triageReportRepository.save(triageReport);
        return TriageReportDto.builder()
                .id(triageReport.getId())
                .patientVisitId(triageReport.getPatientVisit().getId())
                .bpReport(triageReport.getBpReport())
                .temperatureReport(triageReport.getTemperatureReport())
                .pulseReport(triageReport.getPulseReport())
                .weightReport(triageReport.getWeightReport())
                .heightReport(triageReport.getHeightReport())
                .bmiReport(triageReport.getBmiReport())
                .respiratoryRateReport(triageReport.getRespiratoryRateReport())
                .oxygenSaturationReport(triageReport.getOxygenSaturationReport())
                .bloodSugarReport(triageReport.getBloodSugarReport())
                .complaintSummaryReport(triageReport.getComplaintSummaryReport())
                .build();
    }
}
