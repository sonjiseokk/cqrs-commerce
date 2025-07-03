package com.example.ecommerce.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class CategoryDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Category {
        private String id;
        private String name;
        private String slug;
        private String description;
        private Integer level;
        private String imageUrl;
        @Builder.Default
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Category> children = new ArrayList<>();
    }

    @Data
    @Builder
    public static class Detail {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private Integer level;
        private String imageUrl;
        private ParentCategory parent;
    }

    @Data
    @Builder
    public static class ParentCategory {
        private Long id;
        private String name;
        private String slug;
    }


}
