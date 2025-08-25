package com.example.finance_control.infra.exceptions;

import com.example.finance_control.dto.error.ErrorResponse;
import com.example.finance_control.exceptions.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Validation error on path: {} - {}", request.getRequestURI(), ex.getMessage());

        // Extrair erros de cada campo
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Dados de entrada inválidos")
                .details("Verifique os campos e tente novamente")
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found on path: {} - {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("RESOURCE_NOT_FOUND")
                .message("Recurso não encontrado")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex,
            HttpServletRequest request) {

        log.warn("Domain error on path: {} - {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DOMAIN_ERROR")
                .message("Erro nas regras de negócio")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            Exception ex,
            HttpServletRequest request) {

        log.warn("Invalid credentials on path: {} - {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INVALID_CREDENTIALS")
                .message("Credenciais inválidas")
                .details("Email ou senha incorretos")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({UserNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            Exception ex,
            HttpServletRequest request) {

        log.warn("User not found on path: {} - {}", request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USER_NOT_FOUND")
                .message("Usuário não encontrado")
                .details("Verifique o email informado")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            DatabaseException ex,
            HttpServletRequest request) {

        log.error("Database error on path: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DATABASE_ERROR")
                .message("Erro interno do sistema")
                .details("Tente novamente em alguns minutos")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(CreatingTokenException.class)
    public ResponseEntity<ErrorResponse> handleCreatingTokenException(
            CreatingTokenException ex,
            HttpServletRequest request) {

        log.error("Token creation error on path: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("TOKEN_CREATION_ERROR")
                .message("Erro na autenticação")
                .details("Não foi possível gerar o token de acesso")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
            DuplicateEmailException ex,
            HttpServletRequest request) {

        log.warn("Duplicate email attempt on path: {} - {}",
                request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DUPLICATE_EMAIL")
                .message("Email já está em uso")
                .details("Este email já está cadastrado. Tente fazer login ou use outro email.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.CONFLICT.value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error on path: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("Erro interno do servidor")
                .details("Entre em contato com o suporte se o problema persistir")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("HTTP message not readable on path: {} - {}", request.getRequestURI(), ex.getMessage());

        String message = "Dados de entrada inválidos";
        Map<String, String> fieldErrors = new HashMap<>();

        // Detectar se é erro de enum inválido
        if (ex.getCause() instanceof InvalidFormatException invalidFormatEx) {
            if (invalidFormatEx.getTargetType().isEnum()) {
                String fieldName = getFieldNameFromPath(invalidFormatEx.getPath());
                String enumValues = Arrays.toString(invalidFormatEx.getTargetType().getEnumConstants());
                fieldErrors.put(fieldName, "Valor inválido. Valores aceitos: " + enumValues);
            } else {
                // Outros erros de formato (números inválidos, datas inválidas, etc.)
                String fieldName = getFieldNameFromPath(invalidFormatEx.getPath());
                fieldErrors.put(fieldName, "Formato inválido para este campo");
            }
        }
        // Detectar erros de sintaxe JSON
        else if (ex.getCause() instanceof JsonParseException) {
            fieldErrors.put("request", "Formato JSON inválido");
        }
        // Outros erros de deserialização
        else {
            fieldErrors.put("request", "Dados de entrada em formato inválido");
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .details("Verifique os campos e tente novamente")
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    //Método auxiliar para extrair nome do campo do path de erro
    private String getFieldNameFromPath(List<JsonMappingException.Reference> path) {
        if (path.isEmpty()) {
            return "unknown";
        }
        JsonMappingException.Reference lastRef = path.get(path.size() - 1);
        return lastRef.getFieldName() != null ? lastRef.getFieldName() : "unknown";
    }
}