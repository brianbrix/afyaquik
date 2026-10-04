package com.afyaquik.web.api.patients;

import com.afyaquik.communication.PatientCommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient-communications")
@RequiredArgsConstructor
public class PatientCommunicationController {
    private final PatientCommunicationService service;
    @GetMapping("/visit/{id}")
    public PatientCommunicationService.Workspace workspace(@PathVariable Long id) { return service.workspace(id); }
    @PutMapping("/patients/{id}/preferences")
    public void preferences(@PathVariable Long id, @RequestBody PatientCommunicationService.Preferences request) { service.preferences(id, request); }
    @PostMapping("/patients/{id}/messages")
    public PatientCommunicationService.MessageView queue(@PathVariable Long id, @RequestBody PatientCommunicationService.QueueRequest request) { return service.queue(id, request); }
    @PostMapping("/messages/{id}/send")
    public PatientCommunicationService.MessageView send(@PathVariable Long id) { return service.send(id); }
    @PostMapping("/messages/{id}/cancel")
    public PatientCommunicationService.MessageView cancel(@PathVariable Long id) { return service.cancel(id); }
}