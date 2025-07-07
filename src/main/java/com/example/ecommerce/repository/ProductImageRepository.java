package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Modifying(clearAutomatically = false, flushAutomatically = true)
    @Query("""
        delete from ProductImage pi
        where pi.product = :product
    """)
    void deleteByProduct(Product product);
}
