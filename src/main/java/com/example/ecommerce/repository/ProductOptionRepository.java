package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    @Modifying(clearAutomatically = false, flushAutomatically = true)
    @Query("""
        delete from ProductOption po
        where po.optionGroup.product = :product
    """)
    void deleteAllOptionsByProduct(@Param("product") Product product);
}
