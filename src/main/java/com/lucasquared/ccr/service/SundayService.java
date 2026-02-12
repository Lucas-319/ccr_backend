package com.lucasquared.ccr.service;

import com.lucasquared.ccr.domain.attendance.ChildAttendance;
import com.lucasquared.ccr.domain.attendance.ChildAttendanceResponseDTO;
import com.lucasquared.ccr.domain.child.ChildSummaryDTO;
import com.lucasquared.ccr.domain.sunday.SundayAvailability;
import com.lucasquared.ccr.domain.sunday.SundayAvailabilityResponseDTO;
import com.lucasquared.ccr.domain.sunday.SundayCreateDTO;
import com.lucasquared.ccr.domain.sunday.SundayReportDTO;
import com.lucasquared.ccr.domain.sunday.SundayShift;
import com.lucasquared.ccr.domain.sunday.SundaySummaryDTO;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.domain.user.UserSummaryDTO;
import com.lucasquared.ccr.domain.user.UserRole;
import com.lucasquared.ccr.repository.ChildAttendanceRepository;
import com.lucasquared.ccr.repository.SundayAvailabilityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SundayService {

    private static final int MAX_PER_SHIFT = 2;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final SundayAvailabilityRepository repository;
    private final ChildAttendanceRepository attendanceRepository;

    public SundayService(SundayAvailabilityRepository repository, ChildAttendanceRepository attendanceRepository) {
        this.repository = repository;
        this.attendanceRepository = attendanceRepository;
    }

    public SundayAvailabilityResponseDTO createAvailability(User user, SundayCreateDTO dto) {
        LocalDate date = parseDate(dto.date());

        if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must be a Sunday");
        }

        if (repository.existsByDateAndShiftAndUserId(date, dto.shift(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already registered for this shift");
        }

        long count = repository.countByDateAndShift(date, dto.shift());
        if (count >= MAX_PER_SHIFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No slots available for this shift");
        }

        SundayAvailability availability = new SundayAvailability();
        availability.setDate(date);
        availability.setShift(dto.shift());
        availability.setUser(user);

        SundayAvailability saved = repository.save(availability);

        return new SundayAvailabilityResponseDTO(
                saved.getDate().format(DATE_FORMAT),
                saved.getShift(),
                new UserSummaryDTO(user.getId(), user.getName()));
    }

    public List<SundaySummaryDTO> listSummary(String start, String end) {
        LocalDate startDate = parseDate(start);
        LocalDate endDate = parseDate(end);

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        List<SundayAvailability> availabilities = repository.findByDateBetween(startDate, endDate);
        Map<String, List<SundayAvailability>> grouped = new HashMap<>();

        for (SundayAvailability availability : availabilities) {
            String key = buildKey(availability.getDate(), availability.getShift());
            grouped.computeIfAbsent(key, ignore -> new ArrayList<>()).add(availability);
        }

        List<SundaySummaryDTO> result = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                continue;
            }

            for (SundayShift shift : SundayShift.values()) {
                String key = buildKey(date, shift);
                List<SundayAvailability> list = grouped.getOrDefault(key, List.of());

                List<UserSummaryDTO> users = list.stream()
                        .map(a -> new UserSummaryDTO(a.getUser().getId(), a.getUser().getName()))
                        .toList();

                int remaining = Math.max(0, MAX_PER_SHIFT - users.size());

                result.add(new SundaySummaryDTO(
                        date.format(DATE_FORMAT),
                        shift,
                        users,
                        remaining));
            }
        }

        result.sort(Comparator
                .comparing((SundaySummaryDTO summary) -> LocalDate.parse(summary.date(), DATE_FORMAT))
                .thenComparing(SundaySummaryDTO::shift));

        return result;
    }

    public void deleteAvailability(User user, String date, SundayShift shift, String userId) {
        LocalDate parsedDate = parseDate(date);

        if (parsedDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must be a Sunday");
        }

        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        String targetUserId = resolveTargetUserId(user, userId, isAdmin);

        SundayAvailability availability = repository.findByDateAndShiftAndUserId(parsedDate, shift, targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Availability not found"));

        repository.delete(availability);
    }

    public List<SundayReportDTO> listReport(String start, String end, SundayShift shift) {
        LocalDate startDate = parseDate(start);
        LocalDate endDate = resolveEndDate(startDate, end);

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        List<SundayAvailability> availabilities = repository.findByDateBetweenAndShift(startDate, endDate, shift);
        Map<LocalDate, List<SundayAvailability>> availabilityByDate = new HashMap<>();
        for (SundayAvailability availability : availabilities) {
            availabilityByDate.computeIfAbsent(availability.getDate(), ignore -> new ArrayList<>()).add(availability);
        }

        List<ChildAttendance> attendances = attendanceRepository.findByDateBetweenAndShift(startDate, endDate, shift);
        Map<LocalDate, List<ChildAttendance>> attendanceByDate = new HashMap<>();
        for (ChildAttendance attendance : attendances) {
            attendanceByDate.computeIfAbsent(attendance.getDate(), ignore -> new ArrayList<>()).add(attendance);
        }

        List<SundayReportDTO> result = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                continue;
            }

            List<SundayAvailability> availabilityList = availabilityByDate.getOrDefault(date, List.of());
            List<UserSummaryDTO> users = availabilityList.stream()
                    .map(a -> new UserSummaryDTO(a.getUser().getId(), a.getUser().getName()))
                    .toList();

            int remaining = Math.max(0, MAX_PER_SHIFT - users.size());

            List<ChildAttendanceResponseDTO> attendanceResponses = attendanceByDate
                    .getOrDefault(date, List.of()).stream()
                    .map(this::toAttendanceResponse)
                    .toList();

            result.add(new SundayReportDTO(
                    date.format(DATE_FORMAT),
                    shift,
                    users,
                    remaining,
                    attendanceResponses));
        }

        result.sort(Comparator.comparing(report -> LocalDate.parse(report.date(), DATE_FORMAT)));
        return result;
    }

    private String buildKey(LocalDate date, SundayShift shift) {
        return date + "|" + shift.name();
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

    private String resolveTargetUserId(User user, String userId, boolean isAdmin) {
        if (isAdmin) {
            return userId != null ? userId : user.getId();
        }

        if (userId != null && !user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own availability");
        }

        return user.getId();
    }

    private ChildAttendanceResponseDTO toAttendanceResponse(ChildAttendance attendance) {
        return new ChildAttendanceResponseDTO(
                attendance.getDate().format(DATE_FORMAT),
                attendance.getShift(),
                attendance.getPresent(),
                new ChildSummaryDTO(attendance.getChild().getId(), attendance.getChild().getName()),
                new UserSummaryDTO(attendance.getMarkedBy().getId(), attendance.getMarkedBy().getName()),
                attendance.getUpdatedBy() == null
                        ? null
                        : new UserSummaryDTO(attendance.getUpdatedBy().getId(), attendance.getUpdatedBy().getName()),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt());
    }
}
