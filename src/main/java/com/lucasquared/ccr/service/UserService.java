package com.lucasquared.ccr.service;

import com.lucasquared.ccr.domain.user.User;
import com.lucasquared.ccr.domain.user.UserCreateDTO;
import com.lucasquared.ccr.domain.user.UserPasswordUpdateDTO;
import com.lucasquared.ccr.domain.user.UserResponseDTO;
import com.lucasquared.ccr.domain.user.UserUpdateDTO;
import com.lucasquared.ccr.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserCreateDTO dto) {
        if (userRepository.findUserByLogin(dto.login()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists");
        }

        String encryptedPassword = passwordEncoder.encode(dto.password());
        User user = new User(dto.name(), dto.login(), encryptedPassword, dto.role());
        User saved = userRepository.save(user);

        return toResponse(saved);
    }

    public UserResponseDTO updateUser(String id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getLogin().equals(dto.login()) && userRepository.findUserByLogin(dto.login()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists");
        }

        user.setName(dto.name());
        user.setLogin(dto.login());
        user.setRole(dto.role());

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponseDTO getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    public List<UserResponseDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }

    public void updatePassword(String userId, UserPasswordUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is invalid");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getLogin(), user.getRole());
    }
}
