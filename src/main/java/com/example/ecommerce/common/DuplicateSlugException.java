package com.example.ecommerce.common;

public class DuplicateSlugException extends RuntimeException {
    public DuplicateSlugException(String slug) {
        super("이미 존재하는 slug입니다: " + slug);
    }
}
