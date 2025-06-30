package com.example.ecommerce.controller.advice;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.common.DuplicateSlugException;
import com.example.ecommerce.common.ErrorResponse;
import com.example.ecommerce.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ProductControllerAdvise {
    // Request 객체 유효성 검증
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("ProductController 유효성 검사 실패 : ", ex);

        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> details = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.Error.builder()
                        .code(String.valueOf(HttpStatus.BAD_REQUEST))
                        .message("잘못된 파라미터 입력입니다.")
                        .details(details)
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Slug 중복 예외 처리
    @ExceptionHandler(DuplicateSlugException.class)
    public ResponseEntity<?> handleDuplicateSlug(DuplicateSlugException ex) {
        Map<String, String> details = new HashMap<>();
        details.put("slug", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.Error.builder()
                        .code(String.valueOf(HttpStatus.CONFLICT))
                        .message("중복된 slug로 인해 생성할 수 없습니다.")
                        .details(details)
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> details = new HashMap<>();
        details.put("resourceType", ex.getResourceType());
        details.put("resourceId", ex.getResourceId());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.Error.builder()
                        .code(String.valueOf(HttpStatus.NOT_FOUND))
                        .message("요청한 리소스를 찾을 수 없습니다.")
                        .details(details)
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



}
