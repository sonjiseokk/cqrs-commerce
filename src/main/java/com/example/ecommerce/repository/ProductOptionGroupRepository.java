package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductOptionGroup;
import com.example.ecommerce.repository.projection.OptionGroupWithProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Long> {
    @Modifying(clearAutomatically = false, flushAutomatically = true)
    @Query("""
        delete from ProductOptionGroup g
        where g.product = :product
    """)
    void deleteAllGroupsByProduct(@Param("product") Product product);

    @Query("SELECT og.id as id, og.name as name, og.displayOrder as displayOrder, p.id as productId " +
            "FROM ProductOptionGroup og " +
            "JOIN og.product p " +
            "WHERE og.id = :optionGroupId")
    Optional<OptionGroupWithProductProjection> findOptionGroupWithProductProjection(@Param("optionGroupId") Long optionGroupId);
}
