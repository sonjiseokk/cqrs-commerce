package com.example.ecommerce.service.query;

import com.example.ecommerce.common.SortParamUtil;
import com.example.ecommerce.service.dto.PaginationDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class CategoryQuery {
    @Data
    @Builder
    public static class ListCategory {
        Integer level;
    }

    @Data
    @Builder
    public static class CategoryProducts {
        private Long categoryId;
        @Builder.Default
        private boolean includeSubCategories = true;
        private PaginationDto.PaginationRequest request;
    }
}
