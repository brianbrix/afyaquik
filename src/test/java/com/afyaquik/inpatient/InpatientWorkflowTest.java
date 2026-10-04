package com.afyaquik.inpatient;

import com.afyaquik.patients.entity.*;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.patients.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InpatientWorkflowTest {
    private final PatientRepository patients = mock(PatientRepository.class);
    private final PatientVisitRepo visits = mock(PatientVisitRepo.class);
    private final InpatientBedRepository beds = mock(InpatientBedRepository.class);
    private final InpatientAdmissionRepository admissions = mock(InpatientAdmissionRepository.class);
    private final InpatientService service = new InpatientService(patients, visits, beds, admissions);
    private final Patient patient = Patient.builder().id(1L).build();
    private final PatientVisit visit = PatientVisit.builder().id(2L).patient(patient).visitStatus(Status.IN_PROGRESS).build();
    private final InpatientBed bed = new InpatientBed();

    @BeforeEach
    void setup() {
        role("DOCTOR");
        bed.setId(3L); bed.setWard("Synthetic ward"); bed.setCode("Test bed");
        when(patients.findForUpdate(1L)).thenReturn(Optional.of(patient));
        when(visits.findForUpdate(2L)).thenReturn(Optional.of(visit));
        when(admissions.findVisitId(4L)).thenReturn(Optional.of(2L));
        when(beds.findForUpdate(3L)).thenReturn(Optional.of(bed));
        when(admissions.saveAndFlush(any())).thenAnswer(invocation -> { InpatientAdmission admission = invocation.getArgument(0); admission.setId(4L); return admission; });
        when(admissions.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }
    @AfterEach
    void clear() { SecurityContextHolder.clearContext(); }

    @Test
    void admissionOccupiesBedAndDoesNotFinishEncounter() {
        service.admit(new InpatientService.AdmissionRequest(2L, 3L, "Synthetic clinical admission"));
        assertNotNull(bed.getCurrentAdmission());
        assertTrue(visit.isInpatientActive());
        assertEquals(Status.IN_PROGRESS, visit.getVisitStatus());
    }

    @Test
    void occupiedBedOrExistingAdmissionCannotBeAssignedAgain() {
        bed.setCurrentAdmission(new InpatientAdmission());
        assertThrows(IllegalArgumentException.class, () -> service.admit(new InpatientService.AdmissionRequest(2L, 3L, "Synthetic admission")));
        bed.setCurrentAdmission(null);
        when(admissions.findByActivePatientKey(1L)).thenReturn(Optional.of(new InpatientAdmission()));
        assertThrows(IllegalArgumentException.class, () -> service.admit(new InpatientService.AdmissionRequest(2L, 3L, "Synthetic admission")));
    }

    @Test
    void dischargeNeedsClinicalOrderAndReleasesBedWithoutClosingVisit() {
        service.admit(new InpatientService.AdmissionRequest(2L, 3L, "Synthetic admission"));
        InpatientAdmission admission = bed.getCurrentAdmission();
        when(admissions.findById(4L)).thenReturn(Optional.of(admission));
        role("NURSE");
        assertThrows(IllegalArgumentException.class, () -> service.discharge(4L));
        role("DOCTOR");
        service.orderDischarge(4L, new InpatientService.DischargeOrder("Synthetic discharge summary and follow-up recorded"));
        assertNotNull(bed.getCurrentAdmission());
        role("NURSE");
        service.discharge(4L);
        assertNull(bed.getCurrentAdmission());
        assertNull(admission.getActivePatientKey());
        assertFalse(visit.isInpatientActive());
        assertEquals(Status.IN_PROGRESS, visit.getVisitStatus());
        assertEquals(3, admission.getEvents().size());
    }

    @Test
    void receptionCannotAdmitOrCreateDischargeOrders() {
        role("RECEPTIONIST");
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.admit(new InpatientService.AdmissionRequest(2L, 3L, "Synthetic admission")));
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> service.orderDischarge(4L, new InpatientService.DischargeOrder("Synthetic summary")));
    }

    private void role(String name) { SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("synthetic.staff", null, List.of(new SimpleGrantedAuthority("ROLE_" + name)))); }
}