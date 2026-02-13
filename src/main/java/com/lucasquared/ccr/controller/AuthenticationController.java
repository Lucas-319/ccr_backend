package com.lucasquared.ccr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.lucasquared.ccr.domain.user.AuthenticationDTO;
import com.lucasquared.ccr.domain.user.LoginResponseDTO;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.infra.security.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(userNamePassword);

            User user = (User) auth.getPrincipal();
            if (Boolean.FALSE.equals(user.getActive())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User is inactive. Please contact an administrator.");
            }

            var token = tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (DisabledException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is inactive. Please contact an administrator.");
        }
    }

}
