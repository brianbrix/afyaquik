package com.afyaquik.communication;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface PatientMessageRepository extends JpaRepository<PatientMessage, Long> {
    Optional<PatientMessage> findByClientReference(UUID reference);
    List<PatientMessage> findTop50ByPatientIdOrderByIdDesc(Long patientId);
    List<PatientMessage> findByPatientIdAndStatus(Long patientId, String status);
    Optional<PatientMessage> findFirstByPatientIdAndTemplateAndSourceIdAndRecipientAndContentAndStatusIn(
            Long patientId, String template, Long sourceId, String recipient, String content, List<String> statuses);
    @Query("select message.patient.id from PatientMessage message where message.id = :id")
    Optional<Long> findPatientId(@Param("id") Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select message from PatientMessage message where message.id = :id")
    Optional<PatientMessage> findForUpdate(@Param("id") Long id);
}