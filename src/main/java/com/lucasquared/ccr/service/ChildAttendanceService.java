package com.lucasquared.ccr.service;

import com.lucasquared.ccr.domain.attendance.ChildAttendance;
import com.lucasquared.ccr.domain.attendance.ChildAttendanceCreateDTO;
import com.lucasquared.ccr.domain.attendance.ChildAttendanceResponseDTO;
import com.lucasquared.ccr.domain.attendance.ChildAttendanceUpdateDTO;
import com.lucasquared.ccr.domain.child.Child;
import com.lucasquared.ccr.domain.child.ChildSummaryDTO;
import com.lucasquared.ccr.domain.sunday.SundayShift;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.domain.user.UserSummaryDTO;
import com.lucasquared.ccr.repository.ChildAttendanceRepository;
import com.lucasquared.ccr.repository.ChildRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ChildAttendanceService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ChildAttendanceRepository attendanceRepository;
    private final ChildRepository childRepository;

    public ChildAttendanceService(ChildAttendanceRepository attendanceRepository, ChildRepository childRepository) {
        this.attendanceRepository = attendanceRepository;
        this.childRepository = childRepository;
    }

    public ChildAttendanceResponseDTO markAttendance(User user, String childId, ChildAttendanceCreateDTO dto) {
        LocalDate date = parseDate(dto.date());

        if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must be a Sunday");
        }

        if (attendanceRepository.existsByChildIdAndDateAndShift(childId, date, dto.shift())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attendance already registered for this child/date");
        }

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Child not found"));

        ChildAttendance attendance = new ChildAttendance();
        attendance.setDate(date);
        attendance.setShift(dto.shift());
        attendance.setPresent(dto.present());
        attendance.setChild(child);
        attendance.setMarkedBy(user);

        ChildAttendance saved = attendanceRepository.save(attendance);

        return toResponse(saved);
    }

    public List<ChildAttendanceResponseDTO> listAttendanceByDate(String date, SundayShift shift) {
        LocalDate parsedDate = parseDate(date);

        if (parsedDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must be a Sunday");
        }

        return attendanceRepository.findByDateAndShift(parsedDate, shift).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ChildAttendanceResponseDTO> listAttendanceByRange(String start, String end, SundayShift shift) {
        LocalDate startDate = parseDate(start);
        LocalDate endDate = resolveEndDate(startDate, end);

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        return attendanceRepository.findByDateBetweenAndShift(startDate, endDate, shift).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ChildAttendanceResponseDTO> listAttendanceByChildAndRange(
            String childId,
            String start,
            String end,
            SundayShift shift) {
        LocalDate startDate = parseDate(start);
        LocalDate endDate = resolveEndDate(startDate, end);

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        List<ChildAttendance> results = shift == null
                ? attendanceRepository.findByChildIdAndDateBetween(childId, startDate, endDate)
                : attendanceRepository.findByChildIdAndDateBetweenAndShift(childId, startDate, endDate, shift);

        return results.stream()
                .map(this::toResponse)
                .toList();
    }

    public ChildAttendanceResponseDTO updateAttendance(User user, String childId, ChildAttendanceUpdateDTO dto) {
        LocalDate date = parseDate(dto.date());

        if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must be a Sunday");
        }

        ChildAttendance attendance = attendanceRepository.findByChildIdAndDateAndShift(childId, date, dto.shift())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));

        attendance.setPresent(dto.present());
        attendance.setUpdatedBy(user);

        ChildAttendance saved = attendanceRepository.save(attendance);
        return toResponse(saved);
    }

    private ChildAttendanceResponseDTO toResponse(ChildAttendance attendance) {
        Child child = attendance.getChild();
        User markedBy = attendance.getMarkedBy();
        User updatedBy = attendance.getUpdatedBy();

        return new ChildAttendanceResponseDTO(
                attendance.getDate().format(DATE_FORMAT),
                attendance.getShift(),
                attendance.getPresent(),
                new ChildSummaryDTO(child.getId(), child.getName()),
                new UserSummaryDTO(markedBy.getId(), markedBy.getName()),
                updatedBy == null ? null : new UserSummaryDTO(updatedBy.getId(), updatedBy.getName()),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt());
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format (dd/MM/yyyy)");
        }
    }

    private LocalDate resolveEndDate(LocalDate startDate, String end) {
        return end == null || end.isBlank() ? startDate : parseDate(end);
    }
}
