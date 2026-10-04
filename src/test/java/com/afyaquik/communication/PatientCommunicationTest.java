package com.afyaquik.communication;

import com.afyaquik.appointments.repository.AppointmentRepository;
import com.afyaquik.billing.repository.BillPaymentRepository;
import com.afyaquik.patients.entity.*;
import com.afyaquik.patients.repository.*;
import com.afyaquik.users.entity.ContactInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.*;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientCommunicationTest {
    private final PatientRepository patients = mock(PatientRepository.class);
    private final PatientVisitRepo visits = mock(PatientVisitRepo.class);
    private final PatientMessageRepository messages = mock(PatientMessageRepository.class);
    private final AfricaTalkingSmsGateway gateway = mock(AfricaTalkingSmsGateway.class);
    private final TransactionTemplate transactions = mock(TransactionTemplate.class);
    private final PatientCommunicationService service = new PatientCommunicationService(patients, visits, mock(AppointmentRepository.class),
            mock(BillPaymentRepository.class), messages, gateway, transactions);
    private final Patient patient = Patient.builder().id(1L).firstName("Sensitive Name").smsConsent(true).smsConsentPhone("+254712345678")
            .contactInfo(ContactInfo.builder().phoneNumber("+254712345678").build()).build();

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("staff", null, List.of()));
        when(patients.findForUpdate(1L)).thenReturn(Optional.of(patient));
        when(visits.findById(2L)).thenReturn(Optional.of(PatientVisit.builder().patient(patient).nextVisitDate(LocalDate.now(ZoneId.of("Africa/Nairobi")).plusDays(2)).build()));
        when(messages.save(any())).thenAnswer(invocation -> { PatientMessage message = invocation.getArgument(0); message.setId(3L); return message; });
        when(messages.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactions.execute(any())).thenAnswer(invocation -> ((TransactionCallback<?>) invocation.getArgument(0)).doInTransaction(mock(org.springframework.transaction.TransactionStatus.class)));
    }
    @AfterEach
    void clear() { SecurityContextHolder.clearContext(); }

    @Test
    void consentIsRequiredAndTemplatesDoNotContainPatientNames() {
        var request = new PatientCommunicationService.QueueRequest(UUID.randomUUID(), "FOLLOW_UP", 2L);
        patient.setSmsConsent(false);
        assertThrows(IllegalArgumentException.class, () -> service.queue(1L, request));
        patient.setSmsConsent(true);
        var view = service.queue(1L, request);
        assertEquals("QUEUED", view.status());
        assertFalse(view.content().contains("Sensitive"));
        verify(gateway, never()).send(any(), any());
    }

    @Test
    void changedPhoneInvalidatesPreviousConsent() {
        patient.getContactInfo().setPhoneNumber("+254700000000");
        assertThrows(IllegalArgumentException.class, () -> service.queue(1L, new PatientCommunicationService.QueueRequest(UUID.randomUUID(), "FOLLOW_UP", 2L)));
    }

    @Test
    void providerAcceptanceIsNotDeliveryAndRetriesDoNotResend() {
        PatientMessage message = queued();
        when(gateway.send(any(), any())).thenReturn(new AfricaTalkingSmsGateway.Result(true, "provider-id", null));
        assertEquals("ACCEPTED", service.send(3L).status());
        service.send(3L);
        assertEquals("ACCEPTED", message.getStatus());
        verify(gateway, times(1)).send(any(), any());
    }

    @Test
    void ambiguousFailureIsBlockedFromAutomaticResend() {
        queued();
        when(gateway.send(any(), any())).thenThrow(new IllegalStateException("Network timeout"));
        assertEquals("UNKNOWN", service.send(3L).status());
        service.send(3L);
        verify(gateway, times(1)).send(any(), any());
    }

    @Test
    void providerResponseRequiresMatchingRecipientAndMessageId() throws Exception {
        var mapper = new ObjectMapper();
        var response = mapper.readTree("{\"SMSMessageData\":{\"Recipients\":[{\"statusCode\":101,\"number\":\"+254712345678\",\"messageId\":\"provider-id\"}]}}");
        assertTrue(AfricaTalkingSmsGateway.parse(response, "+254712345678").accepted());
        assertThrows(IllegalStateException.class, () -> AfricaTalkingSmsGateway.parse(response, "+254700000000"));
    }

    private PatientMessage queued() {
        PatientMessage message = new PatientMessage();
        message.setId(3L); message.setPatient(patient); message.setRecipient("+254712345678");
        message.setContent(service.queue(1L, new PatientCommunicationService.QueueRequest(UUID.randomUUID(), "FOLLOW_UP", 2L)).content());
        message.setTemplate("FOLLOW_UP"); message.setSourceId(2L); message.setClientReference(UUID.randomUUID());
        when(gateway.configured()).thenReturn(true);
        when(messages.findPatientId(3L)).thenReturn(Optional.of(1L));
        when(messages.findForUpdate(3L)).thenReturn(Optional.of(message));
        when(messages.findById(3L)).thenReturn(Optional.of(message));
        return message;
    }
}