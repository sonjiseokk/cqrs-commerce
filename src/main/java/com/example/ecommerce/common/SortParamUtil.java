package com.example.ecommerce.common;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortParamUtil {

    // snake_case -> camelCase 변환 메소드
    public static String convertToCamelCase(String snakeCase) {
        if (snakeCase.contains("_")) {
            StringBuilder result = new StringBuilder();
            boolean capitalize = false;

            for (char c : snakeCase.toCharArray()) {
                if (c == '_') {
                    capitalize = true;
                } else if (capitalize) {
                    result.append(Character.toUpperCase(c));
                    capitalize = false;
                } else {
                    result.append(c);
                }
            }

            return result.toString();
        }

        return snakeCase; // 이미 camelCase인 경우 그대로 반환
    }

    public static Sort createBasicSortBySortParams(String sort) {
        String[] sortParams = sort.split(":");
        String sortField = SortParamUtil.convertToCamelCase(sortParams[0]); // snake_case -> camelCase 변환
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        return Sort.by(direction, sortField);
    }
}
