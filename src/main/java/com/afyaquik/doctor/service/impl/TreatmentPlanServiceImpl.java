package com.afyaquik.doctor.service.impl;

import com.afyaquik.doctor.dto.ObservationReportDto;
import com.afyaquik.doctor.dto.TreatmentPlanDto;
import com.afyaquik.doctor.dto.TreatmentPlanItemDto;
import com.afyaquik.doctor.dto.TreatmentPlanReportItemDto;
import com.afyaquik.doctor.entity.TreatmentPlan;
import com.afyaquik.doctor.entity.TreatmentPlanItem;
import com.afyaquik.doctor.entity.TreatmentPlanReportItem;
import com.afyaquik.doctor.repository.TreatmentPlanItemRepository;
import com.afyaquik.doctor.repository.TreatmentPlanRepository;
import com.afyaquik.doctor.service.TreatmentPlanService;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.mappers.doctor.TreatmentPlanItemMapper;
import com.afyaquik.utils.mappers.doctor.TreatmentPlanMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentPlanServiceImpl implements TreatmentPlanService {
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentPlanItemRepository treatmentPlanItemRepository;
    private final PatientVisitRepo patientVisitRepo;
    private final TreatmentPlanMapper treatmentPlanMapper;
    private final TreatmentPlanItemMapper treatmentPlanItemMapper;
    private final StationRepository stationRepository;
    @Override
    public TreatmentPlanDto addTreatmentPlan(TreatmentPlanDto treatmentPlanDto) {

        PatientVisit patientVisit = patientVisitRepo.findById(treatmentPlanDto.getPatientVisitId()).orElseThrow(()-> new EntityNotFoundException("Patient visit not found"));
        Station station = stationRepository.findById(treatmentPlanDto.getStationId()).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        List<TreatmentPlanReportItem> treatmentPlanReportItems = treatmentPlanDto.getTreatmentPlanReportItems().stream()
                .map(item -> TreatmentPlanReportItem.builder()
                        .treatmentPlanItem(treatmentPlanItemRepository.findById(item.getTreatmentPlanItemId())
                                .orElseThrow(() -> new EntityNotFoundException("One or more treatment plan items not found")))
                        .reportDetails(item.getReportDetails())
                        .build())
                .toList();
        return treatmentPlanMapper.toDto(treatmentPlanRepository.save(TreatmentPlan.builder()
                .description(treatmentPlanDto.getDescription())
                .patientVisit(patientVisit)
                        .station(station)
                .treatmentPlanReportItems(treatmentPlanReportItems)
                .build()));
    }


    @Override
    public TreatmentPlanDto getTreatmentPlan(Long id) {
        return treatmentPlanMapper.toDto(treatmentPlanRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Treatment plan not found")));
    }

    @Override
    public void deleteTreatmentPlan(Long id) {
        treatmentPlanRepository.findById(id).ifPresent(treatmentPlanRepository::delete);
    }

    @Override
    public TreatmentPlanItemDto addTreatmentPlanItem(TreatmentPlanItemDto treatmentPlanItemDto) {
        return treatmentPlanItemMapper.toDto(treatmentPlanItemRepository.save(TreatmentPlanItem.builder()
                .name(treatmentPlanItemDto.getName())
                .description(treatmentPlanItemDto.getDescription())
                .enabled(treatmentPlanItemDto.isEnabled())
                .build()));
    }

    @Override
    public TreatmentPlanItemDto updateTreatmentPlanItem(Long id, TreatmentPlanItemDto treatmentPlanItemDto) {
        TreatmentPlanItem treatmentPlanItem = treatmentPlanItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Treatment plan item not found"));
        treatmentPlanItem.setName(treatmentPlanItemDto.getName());
        treatmentPlanItem.setDescription(treatmentPlanItemDto.getDescription());
        treatmentPlanItem.setEnabled(treatmentPlanItemDto.isEnabled());
        return treatmentPlanItemMapper.toDto(treatmentPlanItemRepository.save(treatmentPlanItem));
    }

    @Override
    public TreatmentPlanItemDto getTreatmentPlanItem(Long id) {
        return treatmentPlanItemMapper.toDto(treatmentPlanItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Treatment plan item not found")));
    }

    @Override
    public void deleteTreatmentPlanItem(Long id) {
        treatmentPlanItemRepository.findById(id).ifPresent(treatmentPlanItemRepository::delete);
    }

    @Override
    public ListFetchDto<TreatmentPlanDto> getTreatmentPlansForVisit(Long visitId, Pageable pageable) {
        PatientVisit patientVisit = patientVisitRepo.findById(visitId).orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        return ListFetchDto.<TreatmentPlanDto>builder()
                .results(treatmentPlanRepository.findAllByPatientVisitId(patientVisit.getId(), pageable)
                        .map(treatmentPlanMapper::toDto))
                .build();
    }
}
