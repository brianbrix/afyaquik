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
    private final com.afyaquik.billing.repository.CurrencyRepository currencyRepository;
    private final com.afyaquik.billing.service.BillPaymentService paymentService;

    @Override
    @Transactional
    public BillingDto createBilling(BillingDto billingDto) {
        PatientVisit patientVisit = patientVisitRepository.findForUpdate(billingDto.getPatientVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found with id: " + billingDto.getPatientVisitId()));

        // Check if billing already exists for this visit
        if (billingRepository.findByPatientVisit(patientVisit).isPresent()) {
            throw new IllegalStateException("Billing already exists for this patient visit");
        }

        Billing billing = Billing.builder()
                .patientVisit(patientVisit)
                .openingAmount(money(billingDto.getAmount()))
                .amount(money(billingDto.getAmount()))
                .discount(money(billingDto.getDiscount() == null ? BigDecimal.ZERO : billingDto.getDiscount()))
                .currencyCode(currencyRepository.findByActiveTrue().orElseThrow(() -> new IllegalArgumentException("Configure an active billing currency first")).getCode())
                .description(billingDto.getDescription())
                .status(Status.PENDING)
                .build();

            if (patientVisit.isDeleted() || patientVisit.getPatient().isDeleted()) throw new IllegalArgumentException("Archived visits cannot be billed");
            updateBillingAmounts(billing);

        Billing savedBilling = billingRepository.save(billing);
        return mapToDto(savedBilling);
    }

    @Override
    @Transactional
    public BillingDto updateBilling(Long id, BillingDto billingDto) {
        Billing billing = billingRepository.findForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + id));
        requireEditable(billing);
        BigDecimal detailTotal = billing.getBillingDetails().stream().map(BillingDetail::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        billing.setOpeningAmount(money(billingDto.getAmount()).subtract(detailTotal));
        money(billing.getOpeningAmount());
        billing.setDiscount(money(billingDto.getDiscount() == null ? BigDecimal.ZERO : billingDto.getDiscount()));
        if (billing.getCurrencyCode() == null) {
            billing.setCurrencyCode(currencyRepository.findByCode(billingDto.getCurrencyCode())
                    .orElseThrow(() -> new IllegalArgumentException("A reviewed currency code is required for historical bills")).getCode());
        }
        updateBillingAmounts(billing);
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
        Billing billing = billingRepository.findForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + id));
        if (status != Status.PENDING && status != Status.CANCELLED) {
            throw new IllegalArgumentException("Paid status is derived from recorded payments, not a status edit");
        }
        if (billing.isDeleted() || billing.getPayments().stream().anyMatch(payment -> !payment.isReversed() && !payment.isDeleted())) {
            throw new IllegalArgumentException("Reverse active payments before cancelling or reactivating this bill");
        }
        billing.setStatus(status);
        billing.setPaidAt(null);

        Billing updatedBilling = billingRepository.save(billing);
        return mapToDto(updatedBilling);
    }

    @Override
    @Transactional
    public BillingDto recordPayment(Long id, String paymentMethod, String paymentReference) {
        Billing billing = billingRepository.findForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + id));
        paymentService.createPayment(id, billing.getAmountDue(), paymentMethod, paymentReference, null);
        return mapToDto(billing);
    }

    @Override
    @Transactional
    public BillingDetailDto addBillingDetail(Long billingId, BillingDetailDto billingDetailDto) {
        Billing billing = billingRepository.findForUpdate(billingId)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + billingId));
        requireEditable(billing);
        preserveOpeningAmount(billing);

        BillingItem billingItem = billingItemRepository.findById(billingDetailDto.getBillingItemId())
            .orElseThrow(() -> new EntityNotFoundException("Billing item not found with id: " + billingDetailDto.getBillingItemId()));
                        if (!billingItem.isActive() || billingItem.isDeleted() || billingItem.getCurrency() == null
                                || !billingItem.getCurrency().getCode().equals(billing.getCurrencyCode())) {
                            throw new IllegalArgumentException("Select an active billing item in this bill's currency");
                        }

        BillingDetail billingDetail = BillingDetail.builder()
                .billing(billing)
                .billingItem(billingItem)
                .amount(money(billingDetailDto.getAmount() != null ? billingDetailDto.getAmount() : billingItem.getDefaultAmount()))
                .description(billingDetailDto.getDescription())
                .quantity(billingDetailDto.getQuantity() != null ? billingDetailDto.getQuantity() : 1)
                .build();

            if (billingDetail.getQuantity() < 1) throw new IllegalArgumentException("Quantity must be positive");

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
        Billing billing = lockedDetailBill(billingDetailId);
        requireEditable(billing);
        preserveOpeningAmount(billing);
        BillingDetail billingDetail = billingDetailRepository.findById(billingDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Billing detail not found with id: " + billingDetailId));

        // Update billing detail fields
        if (billingDetailDto.getAmount() != null) {
            billingDetail.setAmount(money(billingDetailDto.getAmount()));
        }

        if (billingDetailDto.getDescription() != null) {
            billingDetail.setDescription(billingDetailDto.getDescription());
        }

        if (billingDetailDto.getQuantity() != null) {
            if (billingDetailDto.getQuantity() < 1) throw new IllegalArgumentException("Quantity must be positive");
            billingDetail.setQuantity(billingDetailDto.getQuantity());
        }

        // Calculate total amount
        billingDetail.setTotalAmount(billingDetail.getAmount().multiply(BigDecimal.valueOf(billingDetail.getQuantity())));

        // Update billing amount and total amount
        updateBillingAmounts(billingDetail.getBilling());

        BillingDetail savedBillingDetail = billingDetailRepository.save(billingDetail);
        billingRepository.save(billingDetail.getBilling());

        return billingDetailMapper.toDto(savedBillingDetail);
    }

    @Override
    @Transactional
    public void removeBillingDetail(Long billingDetailId) {
        Billing billing = lockedDetailBill(billingDetailId);
        requireEditable(billing);
        preserveOpeningAmount(billing);
        BillingDetail billingDetail = billingDetailRepository.findById(billingDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Billing detail not found with id: " + billingDetailId));

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
        preserveOpeningAmount(billing);
        // Calculate total amount from billing details
        BigDecimal totalAmount = billing.getOpeningAmount().add(billing.getBillingDetails().stream()
                .map(BillingDetail::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        if (billing.getDiscount() != null) {
            // Reject the request if discount is greater than the amount
            if (billing.getDiscount().compareTo(totalAmount) > 0) {
                throw new IllegalArgumentException("Discount cannot be greater than the total amount");
            }
        }

        billing.setAmount(totalAmount);
        billing.setTotalAmount(totalAmount.subtract(billing.getDiscount() == null ? BigDecimal.ZERO : billing.getDiscount()));

    }

    private BillingDto mapToDto(Billing billing) {
        return billingMapper.toDto(billing);
    }

    private Billing lockedDetailBill(Long detailId) {
        Long billingId = billingDetailRepository.findBillingId(detailId).orElseThrow(() -> new EntityNotFoundException("Billing detail not found"));
        return billingRepository.findForUpdate(billingId).orElseThrow(() -> new EntityNotFoundException("Billing not found"));
    }

    private void requireEditable(Billing billing) {
        if (billing.isDeleted() || billing.getStatus() == Status.CANCELLED || !billing.getPayments().isEmpty()) {
            throw new IllegalArgumentException("Bills with issued receipts cannot be repriced; use a reviewed adjustment workflow");
        }
    }

    private void preserveOpeningAmount(Billing billing) {
        if (billing.getOpeningAmount() == null) {
            BigDecimal details = billing.getBillingDetails().stream().map(BillingDetail::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            billing.setOpeningAmount(money(billing.getAmount().subtract(details)));
        }
    }

    private BigDecimal money(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) throw new IllegalArgumentException("Amounts and discounts must be non-negative");
        try { return amount.setScale(2, java.math.RoundingMode.UNNECESSARY); }
        catch (ArithmeticException exception) { throw new IllegalArgumentException("Use at most two decimal places"); }
    }
}
