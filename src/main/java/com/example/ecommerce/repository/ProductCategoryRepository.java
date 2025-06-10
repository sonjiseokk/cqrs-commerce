package com.example.ecommerce.repository;

import com.example.ecommerce.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
