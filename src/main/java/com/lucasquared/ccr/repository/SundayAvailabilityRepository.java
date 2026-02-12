package com.lucasquared.ccr.repository;

import com.lucasquared.ccr.domain.sunday.SundayAvailability;
import com.lucasquared.ccr.domain.sunday.SundayShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SundayAvailabilityRepository extends JpaRepository<SundayAvailability, String> {

    long countByDateAndShift(LocalDate date, SundayShift shift);

    boolean existsByDateAndShiftAndUserId(LocalDate date, SundayShift shift, String userId);

    Optional<SundayAvailability> findByDateAndShiftAndUserId(LocalDate date, SundayShift shift, String userId);

    List<SundayAvailability> findByDateBetween(LocalDate start, LocalDate end);

    List<SundayAvailability> findByDateBetweenAndShift(LocalDate start, LocalDate end, SundayShift shift);
}
