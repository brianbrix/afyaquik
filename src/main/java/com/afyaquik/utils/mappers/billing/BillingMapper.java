package com.afyaquik.utils.mappers.billing;

import com.afyaquik.billing.dto.BillingDto;
import com.afyaquik.billing.entity.Billing;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.utils.mappers.patients.PatientVisitMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BillingDetailMapper.class, BillPaymentMapper.class, PatientVisitMapper.class})
public abstract class BillingMapper implements EntityMapper<Billing, BillingDto> {

    @Autowired
    protected BillingDetailMapper billingDetailMapper;

    @Autowired
    protected BillPaymentMapper billPaymentMapper;

    @Override
    @Mapping(source = "patientVisit.id", target = "patientVisitId")
    @Mapping(target = "patientName", expression = "java(entity.getPatientVisit().getPatient().getFirstName() + \" \" + entity.getPatientVisit().getPatient().getLastName())")
    @Mapping(source = "billingDetails", target = "billingDetails")
    @Mapping(source = "payments", target = "payments")
    public abstract BillingDto toDto(Billing entity);

    @Named("patientName")
    static String getPatientName(PatientVisit patientVisit) {
        return patientVisit.getPatientName();
    }
    @Mapping(target = "patientName", expression = "java(entity.getPatientVisit().getPatient().getFirstName() + \" \" + entity.getPatientVisit().getPatient().getLastName())")
    public abstract BillingDto toDtoWithPatientName(Billing entity);
}
