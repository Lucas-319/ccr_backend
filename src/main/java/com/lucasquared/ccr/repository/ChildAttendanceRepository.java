package com.lucasquared.ccr.repository;

import com.lucasquared.ccr.domain.attendance.ChildAttendance;
import com.lucasquared.ccr.domain.sunday.SundayShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChildAttendanceRepository extends JpaRepository<ChildAttendance, String> {

    boolean existsByChildIdAndDateAndShift(String childId, LocalDate date, SundayShift shift);

    Optional<ChildAttendance> findByChildIdAndDateAndShift(String childId, LocalDate date, SundayShift shift);

    List<ChildAttendance> findByDateAndShift(LocalDate date, SundayShift shift);

    List<ChildAttendance> findByDateBetweenAndShift(LocalDate start, LocalDate end, SundayShift shift);

    List<ChildAttendance> findByChildIdAndDateBetween(String childId, LocalDate start, LocalDate end);

    List<ChildAttendance> findByChildIdAndDateBetweenAndShift(String childId, LocalDate start, LocalDate end,
            SundayShift shift);
}
