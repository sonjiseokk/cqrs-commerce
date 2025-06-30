package com.example.ecommerce.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private String resourceType;
    private String resourceId;

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("Resource %s with id %d not found", resourceName, id));
        this.resourceType = resourceName;
        this.resourceId = String.valueOf(id);
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("Resource %s with identifier %s not found", resourceName, identifier));
    }
}
