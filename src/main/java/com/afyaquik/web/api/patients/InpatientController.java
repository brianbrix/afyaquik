package com.afyaquik.web.api.patients;

import com.afyaquik.inpatient.InpatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inpatient")
@RequiredArgsConstructor
public class InpatientController {
    private final InpatientService service;
    @GetMapping("/visit/{id}")
    public InpatientService.Workspace workspace(@PathVariable Long id) { return service.workspace(id); }
    @PostMapping("/beds")
    public InpatientService.BedView bed(@RequestBody InpatientService.BedRequest request) { return service.createBed(request); }
    @PostMapping("/admissions")
    public InpatientService.AdmissionView admit(@RequestBody InpatientService.AdmissionRequest request) { return service.admit(request); }
    @PostMapping("/admissions/{id}/transfer")
    public InpatientService.AdmissionView transfer(@PathVariable Long id, @RequestBody InpatientService.TransferRequest request) { return service.transfer(id, request); }
    @PostMapping("/admissions/{id}/discharge-order")
    public InpatientService.AdmissionView order(@PathVariable Long id, @RequestBody InpatientService.DischargeOrder request) { return service.orderDischarge(id, request); }
    @PostMapping("/admissions/{id}/discharge")
    public InpatientService.AdmissionView discharge(@PathVariable Long id) { return service.discharge(id); }
}