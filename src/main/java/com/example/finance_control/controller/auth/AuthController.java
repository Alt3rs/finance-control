package com.example.finance_control.controller.auth;

import com.example.finance_control.domain.user.User;
import com.example.finance_control.dto.*;
import com.example.finance_control.exceptions.DuplicateEmailException;
import com.example.finance_control.exceptions.InvalidCredentialsException;
import com.example.finance_control.exceptions.UserNotFoundException;
import com.example.finance_control.infra.security.TokenService;
import com.example.finance_control.repository.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO body){
        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(passwordEncoder.matches(body.password(), user.getPassword())){
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(token, user.getId()));
        }
        throw new InvalidCredentialsException("Invalid password");
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body) {
        Optional<User> user = this.repository.findByEmail(body.email());

        if (user.isPresent()) {
            throw new DuplicateEmailException(body.email());
        }

        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setEmail(body.email());
        this.repository.save(newUser);

        String token = this.tokenService.generateToken(newUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDTO(token, newUser.getId()));
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponseDTO> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        // Verifica se o header tem o prefixo "Bearer "
        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ValidateResponseDTO(false));
        }

        // Remove o prefixo "Bearer " do token
        String token = authorizationHeader.replace("Bearer ", "");

        // Valida o token
        String emailFromToken = tokenService.validateToken(token);

        if (emailFromToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidateResponseDTO(false));
        }

        // Verifica se o usuário existe no banco de dados
        Optional<User> user = repository.findByEmail(emailFromToken);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidateResponseDTO(false));
        }

        // Token é válido e o usuário existe
        return ResponseEntity.ok(new ValidateResponseDTO(true));
    }

}

