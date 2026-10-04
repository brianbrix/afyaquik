package com.afyaquik.patients.services.impl;

import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.*;
import com.afyaquik.patients.enums.*;
import com.afyaquik.patients.repository.*;
import com.afyaquik.users.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientVisitWorkflowTest {
    private final PatientVisitRepo visits = mock(PatientVisitRepo.class);
    private final PatientAssignmentsRepo assignments = mock(PatientAssignmentsRepo.class);
    private final PatientVisitServiceImpl service = new PatientVisitServiceImpl(null, visits, assignments, null);
    private final PatientVisit visit = PatientVisit.builder().id(1L).patient(Patient.builder().id(1L).firstName("Synthetic").build())
            .visitType(VisitType.CONSULTATION).visitDate(LocalDate.of(2026, 10, 4)).visitStatus(Status.IN_PROGRESS).build();

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @BeforeEach
    void setupLocks() {
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(visit));
        when(assignments.findVisitId(2L)).thenReturn(Optional.of(1L));
    }

    @Test
    void editingAnEncounterDoesNotResetItsClinicalStatus() {
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(visit));
        service.updatePatientVisit(PatientVisitDto.builder().id(1L).summaryReasonForVisit("Updated reason").build());
        assertEquals(Status.IN_PROGRESS, visit.getVisitStatus());
    }

    @Test
    void closedEncountersCannotBeReopened() {
        visit.setVisitStatus(Status.COMPLETED);
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(visit));
        assertThrows(IllegalArgumentException.class, () -> service.updatePatientVisitStatus(1L, "IN_PROGRESS"));
        verify(visits, never()).save(any());
    }

    @Test
    void cancellationDoesNotRecordUnfinishedCareAsCompleted() {
        authenticate("reception", "RECEPTIONIST");
        PatientAssignment pending = PatientAssignment.builder().assignmentStatus(Status.PENDING).build();
        PatientAssignment completed = PatientAssignment.builder().assignmentStatus(Status.COMPLETED).build();
        visit.getPatientAssignments().addAll(List.of(pending, completed));
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(visit));
        service.updatePatientVisitStatus(1L, "CANCELLED");
        assertEquals(Status.CANCELLED, pending.getAssignmentStatus());
        assertEquals(Status.COMPLETED, completed.getAssignmentStatus());
    }

    @Test
    void paymentStatusCannotBeUsedAsClinicalStatus() {
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(visit));
        assertThrows(IllegalArgumentException.class, () -> service.updatePatientVisitStatus(1L, "PAID"));
    }

    @Test
    void stationCompletionDoesNotCloseTheWholeEncounter() {
        authenticate("nurse", "NURSE");
        PatientAssignment assignment = PatientAssignment.builder().patientVisit(visit)
                .assignedOfficer(User.builder().username("nurse").build()).build();
        visit.getPatientAssignments().add(assignment);
        when(assignments.findByIdWithVisitAndAssignments(2L)).thenReturn(Optional.of(assignment));
        service.updateAssignmentStatus(2L, "COMPLETED");
        assertEquals(Status.COMPLETED, assignment.getAssignmentStatus());
        assertEquals(Status.IN_PROGRESS, visit.getVisitStatus());
    }

    @Test
    void unrelatedStaffCannotCompleteAnotherOfficersAssignment() {
        authenticate("other", "DOCTOR");
        var assignment = PatientAssignment.builder().patientVisit(visit).assignedOfficer(User.builder().username("nurse").build()).build();
        when(assignments.findByIdWithVisitAndAssignments(2L)).thenReturn(Optional.of(assignment));
        assertThrows(AccessDeniedException.class, () -> service.updateAssignmentStatus(2L, "COMPLETED"));
    }

    @Test
    void encounterClosureRequiresAClinicianAndCompletedAssignments() {
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(visit));
        authenticate("reception", "RECEPTIONIST");
        assertThrows(AccessDeniedException.class, () -> service.updatePatientVisitStatus(1L, "COMPLETED"));
        authenticate("doctor", "DOCTOR");
        visit.getPatientAssignments().add(PatientAssignment.builder().build());
        assertThrows(IllegalArgumentException.class, () -> service.updatePatientVisitStatus(1L, "COMPLETED"));
    }

    private void authenticate(String username, String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }

    @Test
    void activeAdmissionBlocksEncounterClosure() {
        authenticate("doctor", "DOCTOR");
        visit.setInpatientActive(true);
        assertThrows(IllegalArgumentException.class, () -> service.updatePatientVisitStatus(1L, "COMPLETED"));
    }

    @Test
    void pharmacistsCannotCancelWholeEncounters() {
        authenticate("pharmacist", "PHARMACIST");
        assertThrows(AccessDeniedException.class, () -> service.updatePatientVisitStatus(1L, "CANCELLED"));
    }
}