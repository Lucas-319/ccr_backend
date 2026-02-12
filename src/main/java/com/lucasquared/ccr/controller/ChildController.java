package com.lucasquared.ccr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucasquared.ccr.domain.attendance.ChildAttendanceCreateDTO;
import com.lucasquared.ccr.domain.attendance.ChildAttendanceResponseDTO;
import com.lucasquared.ccr.domain.attendance.ChildAttendanceUpdateDTO;
import com.lucasquared.ccr.domain.child.ChildCreateDTO;
import com.lucasquared.ccr.domain.child.ChildResponseDTO;
import com.lucasquared.ccr.domain.child.ChildUpdateDTO;
import com.lucasquared.ccr.domain.sunday.SundayShift;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.service.ChildAttendanceService;
import com.lucasquared.ccr.service.ChildService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/children")
public class ChildController {

    private final ChildService childService;
    private final ChildAttendanceService childAttendanceService;

    public ChildController(ChildService childService, ChildAttendanceService childAttendanceService) {
        this.childService = childService;
        this.childAttendanceService = childAttendanceService;
    }

    @PostMapping
    public ResponseEntity<ChildResponseDTO> createChild(@AuthenticationPrincipal User user,
            @RequestBody @Valid ChildCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(childService.createChild(user, dto));
    }

    @GetMapping
    public ResponseEntity<List<ChildResponseDTO>> listChildren() {
        return ResponseEntity.ok(childService.listChildren());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChildResponseDTO>> searchChildren(@RequestParam String name) {
        return ResponseEntity.ok(childService.searchByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChildResponseDTO> getChild(@PathVariable String id) {
        return ResponseEntity.ok(childService.getChild(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChildResponseDTO> updateChild(@PathVariable String id, @AuthenticationPrincipal User user,
            @RequestBody @Valid ChildUpdateDTO dto) {
        return ResponseEntity.ok(childService.updateChild(id, user, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChild(@PathVariable String id) {
        childService.deleteChild(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/attendance")
    public ResponseEntity<ChildAttendanceResponseDTO> markAttendance(
            @PathVariable String id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ChildAttendanceCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(childAttendanceService.markAttendance(user, id, dto));
    }

    @PutMapping("/{id}/attendance")
    public ResponseEntity<ChildAttendanceResponseDTO> updateAttendance(
            @PathVariable String id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ChildAttendanceUpdateDTO dto) {
        return ResponseEntity.ok(childAttendanceService.updateAttendance(user, id, dto));
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<ChildAttendanceResponseDTO>> listAttendanceByRange(
            @RequestParam String start,
            @RequestParam(required = false) String end,
            @RequestParam SundayShift shift) {
        return ResponseEntity.ok(childAttendanceService.listAttendanceByRange(start, end, shift));
    }

    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<ChildAttendanceResponseDTO>> listAttendanceByChild(
            @PathVariable String id,
            @RequestParam String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) SundayShift shift) {
        return ResponseEntity.ok(childAttendanceService.listAttendanceByChildAndRange(id, start, end, shift));
    }
}
