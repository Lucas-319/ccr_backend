package com.lucasquared.ccr.controller;

import com.lucasquared.ccr.domain.user.AuthenticationDTO;
import com.lucasquared.ccr.domain.user.RegisterDTO;
import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
       if(this.userRepository.findByLogin(data.login()) != null) {
           return ResponseEntity.badRequest().body("Login already exists");
       }

       String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
       User user = new User(data.name(), data.login(), encryptedPassword, data.role());

       this.userRepository.save(user);

       return ResponseEntity.ok().build();
    }
}
