package com.afyaquik.web.api.reports;

import com.afyaquik.reports.FacilityReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('REPORTS', 'ADMIN', 'SUPERADMIN', 'DOCTOR', 'NURSE')")
public class FacilityReportController {
    private final FacilityReportingService reportingService;

    @GetMapping("/summary")
    public FacilityReportingService.Summary summary(@RequestParam(required = false) LocalDate from,
                                                    @RequestParam(required = false) LocalDate to) {
        LocalDate today = LocalDate.now(ZoneId.of("Africa/Nairobi"));
        return reportingService.summarize(from == null ? today.withDayOfMonth(1) : from, to == null ? today : to);
    }
}