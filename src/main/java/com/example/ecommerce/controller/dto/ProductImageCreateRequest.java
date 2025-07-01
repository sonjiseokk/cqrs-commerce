package com.example.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageCreateRequest {
    private String url;
    @JsonProperty("alt_text")
    private String altText;
    @JsonProperty("is_primary")
    private boolean isPrimary;
    private Integer displayOrder;
    private Long optionId;
}
