package com.afyaquik.patients.services;

import com.afyaquik.dtos.patient.TriageReportDto;

public interface TriageService {
    TriageReportDto createTriageReport(TriageReportDto triageReportDto);
    TriageReportDto updateTriageReport(TriageReportDto triageReportDto);
}
