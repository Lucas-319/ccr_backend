package com.lucasquared.ccr.controller;

import com.lucasquared.ccr.domain.sunday.SundayAvailabilityResponseDTO;
import com.lucasquared.ccr.domain.sunday.SundayCreateDTO;
import com.lucasquared.ccr.domain.sunday.SundayReportDTO;
import com.lucasquared.ccr.domain.sunday.SundayShift;
import com.lucasquared.ccr.domain.sunday.SundaySummaryDTO;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.service.SundayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sundays")
public class SundayController {

    private final SundayService sundayService;

    public SundayController(SundayService sundayService) {
        this.sundayService = sundayService;
    }

    @PostMapping
    public ResponseEntity<SundayAvailabilityResponseDTO> create(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid SundayCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sundayService.createAvailability(user, dto));
    }

    @GetMapping
    public ResponseEntity<List<SundaySummaryDTO>> listSummary(
            @RequestParam String start,
            @RequestParam String end) {
        return ResponseEntity.ok(sundayService.listSummary(start, end));
    }

    @GetMapping("/report")
    public ResponseEntity<List<SundayReportDTO>> listReport(
            @RequestParam String start,
            @RequestParam(required = false) String end,
            @RequestParam SundayShift shift) {
        return ResponseEntity.ok(sundayService.listReport(start, end, shift));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAvailability(
            @AuthenticationPrincipal User user,
            @RequestParam String date,
            @RequestParam SundayShift shift,
            @RequestParam(required = false) String userId) {
        sundayService.deleteAvailability(user, date, shift, userId);
        return ResponseEntity.noContent().build();
    }
}
