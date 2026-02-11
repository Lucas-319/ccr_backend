package com.lucasquared.ccr.service;

import com.lucasquared.ccr.domain.child.Child;
import com.lucasquared.ccr.domain.child.ChildCreateDTO;
import com.lucasquared.ccr.domain.child.ChildResponseDTO;
import com.lucasquared.ccr.domain.child.ChildUpdateDTO;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.domain.user.UserSummaryDTO;
import com.lucasquared.ccr.repository.ChildRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChildService {

    private final ChildRepository childRepository;

    public ChildService(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    public ChildResponseDTO createChild(User user, ChildCreateDTO dto) {
        Child child = new Child();
        child.setName(dto.name());
        child.setResponsibleName(dto.responsibleName());
        child.setResponsibleContact(dto.responsibleContact());
        child.setAllergies(dto.allergies());
        child.setCreatedBy(user);
        child.setUpdatedBy(user);

        Child saved = childRepository.save(child);
        return toResponse(saved);
    }

    public ChildResponseDTO updateChild(String id, User user, ChildUpdateDTO dto) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Child not found"));

        child.setName(dto.name());
        child.setResponsibleName(dto.responsibleName());
        child.setResponsibleContact(dto.responsibleContact());
        child.setAllergies(dto.allergies());
        child.setUpdatedBy(user);

        Child saved = childRepository.save(child);
        return toResponse(saved);
    }

    public ChildResponseDTO getChild(String id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Child not found"));
        return toResponse(child);
    }

    public List<ChildResponseDTO> listChildren() {
        return childRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteChild(String id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Child not found"));
        childRepository.delete(child);
    }

    private ChildResponseDTO toResponse(Child child) {
        UserSummaryDTO createdBy = new UserSummaryDTO(child.getCreatedBy().getId(), child.getCreatedBy().getName());
        UserSummaryDTO updatedBy = child.getUpdatedBy() == null
                ? null
                : new UserSummaryDTO(child.getUpdatedBy().getId(), child.getUpdatedBy().getName());

        return new ChildResponseDTO(
                child.getId(),
                child.getName(),
                child.getResponsibleName(),
                child.getResponsibleContact(),
                child.getAllergies(),
                createdBy,
                updatedBy,
                child.getCreatedAt(),
                child.getUpdatedAt());
    }
}
