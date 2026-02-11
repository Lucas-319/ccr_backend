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
import org.springframework.web.bind.annotation.RestController;

import com.lucasquared.ccr.domain.child.ChildCreateDTO;
import com.lucasquared.ccr.domain.child.ChildResponseDTO;
import com.lucasquared.ccr.domain.child.ChildUpdateDTO;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.service.ChildService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/children")
public class ChildController {

    private final ChildService childService;

    public ChildController(ChildService childService) {
        this.childService = childService;
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
}
