package com.example.ecommerce.service.dto;

import com.example.ecommerce.common.SortParamUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationRequest {
        @Builder.Default
        private int page = 1;
        @Builder.Default
        private int size = 10;
        @Builder.Default
        private String sort = "created_at:desc";

        public Pageable toPageable() {
            return PageRequest.of(
                    page - 1,
                    size,
                    SortParamUtil.createBasicSortBySortParams(sort)
            );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {
        private Integer totalItems;
        private Integer totalPages;
        private Integer currentPage;
        private Integer perPage;
    }
}
