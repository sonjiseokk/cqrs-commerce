package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Long> {
    @Modifying(clearAutomatically = false, flushAutomatically = true)
    @Query("""
        delete from ProductOptionGroup g
        where g.product = :product
    """)
    void deleteAllGroupsByProduct(@Param("product") Product product);
}
