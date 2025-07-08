package com.example.ecommerce.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
public class UnauthorizedReviewException extends RuntimeException {
    private String message;
    public UnauthorizedReviewException(String message) {
        this.message = message;
    }
}
