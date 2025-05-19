package com.afyaquik.web.api.appointments;

import com.afyaquik.appointments.services.AppointmentService;
import com.afyaquik.dtos.appointments.AppointmentDto;
import com.afyaquik.dtos.search.ListFetchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<AppointmentDto> create(@RequestBody AppointmentDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ListFetchDto<AppointmentDto>> getByDoctor(@PathVariable Long doctorId, Pageable pageable) {
        return ResponseEntity.ok(service.getDoctorAppointments(doctorId, pageable));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ListFetchDto<AppointmentDto>> getByPatient(@PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(service.getPatientAppointments(patientId, pageable));
    }
}
