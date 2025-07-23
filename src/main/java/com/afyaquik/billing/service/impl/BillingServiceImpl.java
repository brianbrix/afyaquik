package com.afyaquik.billing.service.impl;

import com.afyaquik.billing.dto.BillingDetailDto;
import com.afyaquik.billing.dto.BillingDto;
import com.afyaquik.billing.entity.Billing;
import com.afyaquik.billing.entity.BillingDetail;
import com.afyaquik.billing.entity.BillingItem;
import com.afyaquik.billing.repository.BillingDetailRepository;
import com.afyaquik.billing.repository.BillingItemRepository;
import com.afyaquik.billing.repository.BillingRepository;
import com.afyaquik.billing.service.BillingService;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.utils.mappers.billing.BillingDetailMapper;
import com.afyaquik.utils.mappers.billing.BillingMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final PatientVisitRepo patientVisitRepository;
    private final BillingItemRepository billingItemRepository;
    private final BillingDetailRepository billingDetailRepository;
    private final BillingMapper billingMapper;
    private final BillingDetailMapper billingDetailMapper;

    @Override
    @Transactional
    public BillingDto createBilling(BillingDto billingDto) {
        PatientVisit patientVisit = patientVisitRepository.findById(billingDto.getPatientVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found with id: " + billingDto.getPatientVisitId()));

        // Check if billing already exists for this visit
        if (billingRepository.findByPatientVisit(patientVisit).isPresent()) {
            throw new IllegalStateException("Billing already exists for this patient visit");
        }

        Billing billing = Billing.builder()
                .patientVisit(patientVisit)
                .amount(billingDto.getAmount())
                .discount(billingDto.getDiscount())
                .totalAmount(billingDto.getTotalAmount())
                .description(billingDto.getDescription())
                .status(Status.PENDING)
                .build();

        Billing savedBilling = billingRepository.save(billing);
        return mapToDto(savedBilling);
    }

    @Override
    @Transactional
    public BillingDto updateBilling(Long id, BillingDto billingDto) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + id));

        billing.setAmount(billingDto.getAmount());
        billing.setDiscount(billingDto.getDiscount());
        billing.setTotalAmount(billingDto.getTotalAmount());

        if (billing.getDiscount() != null) {
            // Reject the request if discount is greater than the amount
            if (billing.getDiscount().compareTo(billingDto.getAmount()) > 0) {
                throw new IllegalArgumentException("Discount cannot be greater than the total amount");
            }
            billing.setTotalAmount(billingDto.getAmount().subtract(billing.getDiscount()));
        }
        billing.setDescription(billingDto.getDescription());

        Billing updatedBilling = billingRepository.save(billing);
        return mapToDto(updatedBilling);
    }

    @Override
    public BillingDto getBillingById(Long id) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + id));
        return mapToDto(billing);
    }

    @Override
    public BillingDto getBillingByPatientVisitId(Long patientVisitId) {
        PatientVisit patientVisit = patientVisitRepository.findById(patientVisitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found with id: " + patientVisitId));

        Billing billing = billingRepository.findByPatientVisit(patientVisit)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found for patient visit with id: " + patientVisitId));

        return mapToDto(billing);
    }

    @Override
    public List<BillingDto> getAllBillings() {
        return billingRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingDto> getBillingsByStatus(Status status) {
        return billingRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingDto> getBillingsByPatientId(Long patientId) {
        return billingRepository.findByPatientVisit_Patient_Id(patientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BillingDto updateBillingStatus(Long id, Status status) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + id));
        if (billing.getStatus() == Status.PAID) {
            throw new IllegalArgumentException("Billing is already paid");
        }

        billing.setStatus(status);

        // If status is PAID, set the paidAt timestamp
        if (status == Status.PAID) {
            billing.setPaidAt(LocalDateTime.now());
        }

        Billing updatedBilling = billingRepository.save(billing);
        return mapToDto(updatedBilling);
    }

    @Override
    @Transactional
    public BillingDto recordPayment(Long id, String paymentMethod, String paymentReference) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + id));

        billing.setPaymentMethod(paymentMethod);
        billing.setPaymentReference(paymentReference);
        billing.setStatus(Status.PAID);
        billing.setPaidAt(LocalDateTime.now());

        Billing updatedBilling = billingRepository.save(billing);
        return mapToDto(updatedBilling);
    }

    @Override
    @Transactional
    public BillingDetailDto addBillingDetail(Long billingId, BillingDetailDto billingDetailDto) {
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + billingId));

        BillingItem billingItem = billingItemRepository.findById(billingDetailDto.getBillingItemId())
                .orElseThrow(() -> new EntityNotFoundException("Billing item not found with id: " + billingDetailDto.getBillingItemId()));

        BillingDetail billingDetail = BillingDetail.builder()
                .billing(billing)
                .billingItem(billingItem)
                .amount(billingDetailDto.getAmount() != null ? billingDetailDto.getAmount() : billingItem.getDefaultAmount())
                .description(billingDetailDto.getDescription())
                .quantity(billingDetailDto.getQuantity() != null ? billingDetailDto.getQuantity() : 1)
                .build();

        // Calculate total amount
        billingDetail.setTotalAmount(billingDetail.getAmount().multiply(BigDecimal.valueOf(billingDetail.getQuantity())));

        // Add billing detail to billing
        billing.addBillingDetail(billingDetail);

        // Update billing amount and total amount
        updateBillingAmounts(billing);

        billingRepository.save(billing);
        return billingDetailMapper.toDto(billingDetail);
    }

    @Override
    @Transactional
    public BillingDetailDto updateBillingDetail(Long billingDetailId, BillingDetailDto billingDetailDto) {
        BillingDetail billingDetail = billingDetailRepository.findById(billingDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Billing detail not found with id: " + billingDetailId));

        // Update billing detail fields
        if (billingDetailDto.getAmount() != null) {
            billingDetail.setAmount(billingDetailDto.getAmount());
        }

        if (billingDetailDto.getDescription() != null) {
            billingDetail.setDescription(billingDetailDto.getDescription());
        }

        if (billingDetailDto.getQuantity() != null) {
            billingDetail.setQuantity(billingDetailDto.getQuantity());
        }

        // Calculate total amount
        if (billingDetailDto.getTotalAmount()!=null) {
            billingDetail.setTotalAmount(billingDetail.getAmount().multiply(BigDecimal.valueOf(billingDetail.getQuantity())));
        }

        // Update billing amount and total amount
        updateBillingAmounts(billingDetail.getBilling());

        BillingDetail savedBillingDetail = billingDetailRepository.save(billingDetail);
        billingRepository.save(billingDetail.getBilling());

        return billingDetailMapper.toDto(savedBillingDetail);
    }

    @Override
    @Transactional
    public void removeBillingDetail(Long billingDetailId) {
        BillingDetail billingDetail = billingDetailRepository.findById(billingDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Billing detail not found with id: " + billingDetailId));

        Billing billing = billingDetail.getBilling();

        // Remove billing detail from billing
        billing.removeBillingDetail(billingDetail);

        // Update billing amount and total amount
        updateBillingAmounts(billing);

        billingRepository.save(billing);
    }

    @Override
    public List<BillingDetailDto> getBillingDetailsByBillingId(Long billingId) {
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + billingId));

        if (billing.getBillingDetails()==null)
        {
            return  new ArrayList<>();
        }
        return billing.getBillingDetails().stream()
                .map(billingDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    private void updateBillingAmounts(Billing billing) {
        // Calculate total amount from billing details
        BigDecimal totalAmount = billing.getBillingDetails().stream()
                .map(BillingDetail::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (billing.getDiscount() != null) {
            // Reject the request if discount is greater than the amount
            if (billing.getDiscount().compareTo(totalAmount) > 0) {
                throw new IllegalArgumentException("Discount cannot be greater than the total amount");
            }
        }

        billing.setAmount(totalAmount);
        billing.setTotalAmount(totalAmount);

    }

    private BillingDto mapToDto(Billing billing) {
        return billingMapper.toDto(billing);
    }
}
