package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    @Modifying(clearAutomatically = false, flushAutomatically = true)
    @Query("""
        delete from ProductCategory pc
        where pc.product = :product
    """)
    void deleteByProduct(Product product);
}

