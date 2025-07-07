package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByProduct(Product product);

    @Query("select r from Review r " +
            "where r.product = :product " +
            "and r.rating = :rating" )
    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByProductRating(@Param("product") Product product,
                                     @Param("rating") int rating,
                                     Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByProduct(Product product, Pageable pageable);
}
