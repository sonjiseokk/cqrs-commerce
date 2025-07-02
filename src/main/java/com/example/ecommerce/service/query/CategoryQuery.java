package com.example.ecommerce.service.query;

import lombok.Builder;
import lombok.Data;

public class CategoryQuery {
    @Data
    @Builder
    public static class ListCategory {
        Integer level;
    }
}
