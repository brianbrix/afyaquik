package com.afyaquik.web.api.reports;

import com.afyaquik.reports.Dhis2ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/dhis2")
@RequiredArgsConstructor
public class Dhis2Controller {
    private final Dhis2ExportService service;
    @GetMapping("/config")
    public Dhis2ExportService.Configuration configuration() { return service.configuration(); }
    @GetMapping("/export")
    public Dhis2ExportService.DataValueSet export(@RequestParam String period) { return service.export(period); }
}